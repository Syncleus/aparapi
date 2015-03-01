package com.hp.aparapi.sample.kmeans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import java.util.Random;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

class KMeans {

    public int nrVectors;
    public int nrClusters;
    public int dim;
    public float[] vectors;
    public float[] clusters;

    public int[] clusterAssignedCPU;
    public int[] clusterAssignedGPU;

    static public Random rand = new Random();
    
    KMeans(int nrVectors, int nrClusters, int dim) {
        this.nrVectors = nrVectors;
        this.nrClusters = nrClusters;
        this.dim = dim;

        this.vectors = new float[dim * nrVectors];
        this.clusters = new float[dim * nrClusters];

        this.clusterAssignedCPU = new int[vectors.length];
        this.clusterAssignedGPU = new int[vectors.length];
    }

    void genVectors() {
        float radius;
        for (int i = 0; i < nrClusters * dim; i++) {
            clusters[i] = 1000 * (float)rand.nextDouble();
        }

        //
        // each cluster has stddev of 30
        //
        for (int i = 0; i < nrVectors; i++) {
            int clusterId = rand.nextInt(nrClusters);
            for (int j = 0; j < dim; j++) {
                vectors[i * dim + j] = clusters[clusterId * dim + j] + (float)rand.nextGaussian() * 30;
            }
        }
    }

    void populateData() {
        for (int i = 0; i < nrClusters * dim; i++) {
            clusters[i] = clusters[i] * 2;
        }

        for (int i = 0; i < nrVectors * dim; i++) {
            vectors[i] = vectors[i] * 2;
        }

        for (int i = 0; i < nrClusters * dim; i++) {
            clusters[i] = clusters[i] / 2;
        }

        for (int i = 0; i < nrVectors * dim; i++) {
            vectors[i] = vectors[i] / 2;
        }
    }

    void runKmeansGPU() {
        final int nrCluster = nrClusters;
        final int vecSize = dim;
        final int clusterVecSize = clusters.length;
        final float[] inVecs = vectors;
        final float[] inCluster = clusters;
        final int[] clusterAssigned = clusterAssignedGPU;

        final Range range = Range.create(nrVectors, 16);
        final float[] cluster_$local$ = new float[inCluster.length];
        
        Kernel kernel = new Kernel(){
                
                @Override
                public void run() {
                    int gid = getGlobalId(0);
                    int lid = getLocalId(0);
                    int lsize = getLocalSize(0);
                    
                    for (int i = lid; i < clusterVecSize ; i += lsize) {
                         cluster_$local$[i] = inCluster[i];
                    }
                    
                    localBarrier();
                    
                    int vecBegin = gid * vecSize;
                    int minCluster = -1;
                    float minDist = Float.MAX_VALUE;

                    for (int iC = 0; iC < nrCluster; iC++) {
                        float sum = 0.0f;
                        for (int iElm = 0; iElm < vecSize; iElm++) {
                            float diff = inVecs[vecBegin + iElm] - cluster_$local$[iC * vecSize + iElm];
                            sum += diff * diff;
                        }
                        // !!! oren change  -> this cmd will force use of doubles 
                        //float dist = (float)Math.sqrt(sum);
                        float dist = sqrt(sum);
                        
                        if (dist < minDist) {
                            minCluster = iC;
                            minDist = dist;
                        }
                    }

                    clusterAssigned[gid] = minCluster;
                    // this.atomicAdd(s0s, minCluster, 1);                                                                                                                                               

                    // int indexBegin = minCluster * vecSize;                                                                                                                                            
                    // for (int i = 0; i < vecSize; i++) {                                                                                                                                               
                    //     float elm = inVecs[vecBegin + i];                                                                                                                                             
                    //     s1s[indexBegin + i] += elm;                                                                                                                                                   
                    //     s2s[indexBegin + i] += elm * elm;                                                                                                                                             
                    // }                                                                                                                                                                                 
                }
            };
        
        kernel.execute(range);
    }

    void runKmeansCPU() {
        final int nrCluster = nrClusters;
        final int vecSize = dim;
        final int clusterVecSize = clusters.length;
        final float[] inVecs = vectors;
        final float[] inCluster = clusters;
        final int[] clusterAssigned = clusterAssignedCPU;

        for (int gid = 0; gid < nrVectors; gid++) {
            int vecBegin = gid * vecSize;
            int minCluster = -1;
            float minDist = Float.MAX_VALUE;
            
            for (int iC = 0; iC < nrCluster; iC++) {
                float sum = 0.0f;
                for (int iElm = 0; iElm < vecSize; iElm++) {
                    float diff = inVecs[vecBegin + iElm] - inCluster[iC * vecSize + iElm];
                    sum += diff * diff;
                }
                float dist = (float)Math.sqrt(sum);
                
                if (dist < minDist) {
                    minCluster = iC;
                    minDist = dist;
                }
            }
            clusterAssigned[gid] = minCluster;
        }
    }

    void checkResults() {
        for (int i = 0; i < clusterAssignedCPU.length; i++) {
            if (clusterAssignedCPU[i] != clusterAssignedGPU[i]) {
                System.err.println(String.format("Cluster differs: CPU[%d]=%d, GPU[%d]=%d",
                                                 i, clusterAssignedCPU[i],
                                                 i, clusterAssignedGPU[i]));
            }
        }
    }

}
