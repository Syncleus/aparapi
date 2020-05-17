/*
 * For a description of the algorithm and the terms used, please see the
 * documentation for this sample.
 *
 * Each work-item invocation of this kernel, calculates the position for 
 * one particle
 *
 * Work-items use local memory to reduce memory bandwidth and reuse of data
 */

__kernel
void 
nbody_sim(
    __global float4* pos,
    __global float4* scaled, // Gary added to try to scale 
    __global float4* vel,
    int numBodies,
    float deltaTime,
    float epsSqr,
    int width,
    int height, 
    __local float4* localPos)
{
    unsigned int tid = get_local_id(0);
    unsigned int gid = get_global_id(0);
    unsigned int localSize = get_local_size(0);

    unsigned int numTiles = numBodies / localSize;

    float4 myPos = pos[gid];
    float4 acc = (float4)(0.0f, 0.0f, 0.0f, 0.0f);

    for(int i = 0; i < numTiles; ++i)
    {
        // load one tile into local memory
        int idx = i * localSize + tid;
        localPos[tid] = pos[idx];

        // Synchronize to make sure data is available for processing
        barrier(CLK_LOCAL_MEM_FENCE);

        // calculate acceleration effect due to each body
        // a[i->j] = m[j] * r[i->j] / (r^2 + epsSqr)^(3/2)
       for(int j = 0; j < localSize; ++j)
        {
            // Calculate acceleartion caused by particle j on particle i
            float4 r = localPos[j] - myPos;
            float distSqr = r.x * r.x  +  r.y * r.y  +  r.z * r.z;
            float invDist = 1.0f / sqrt(distSqr + epsSqr);
            //float invDist = 1.0f / sqrt(distSqr + 50.f);
            float invDistCube = invDist * invDist * invDist;
            float s = localPos[j].w * invDistCube;

            // accumulate effect of all particles
            acc += s * r;
        }

        // Synchronize so that next tile can be loaded
       barrier(CLK_LOCAL_MEM_FENCE);
    }

    float4 oldVel = vel[gid];

    // updated position and velocity
    float4 newPos = myPos + oldVel * deltaTime + acc * 0.5f * deltaTime * deltaTime;
    //newPos.w = (float)localSize;

    float4 newVel = oldVel + acc * deltaTime;

    // write to global memory
    pos[gid] = newPos;
    float d = newPos.z / 800.0f;
    scaled[gid].w =  (newPos.w/800.0f) / (d*d) ;    // use mass/distance^2 to create illusion of depth
    scaled[gid].x =  width/2+newPos.x-scaled[gid].w;
    scaled[gid].y =  height/2+newPos.y-scaled[gid].w;
    vel[gid] = newVel;
}
