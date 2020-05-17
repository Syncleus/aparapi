package com.amd.aparapi;

import java.util.function.IntConsumer;

/**
 * Created with IntelliJ IDEA.
 * User: user1
 * Date: 9/5/13
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class HybridDevice extends Device {
    HSADevice hsaDevice = (HSADevice)Device.hsa();
    JavaThreadPoolDevice jtpDevice = (JavaThreadPoolDevice)Device.jtp();
    @Override
    public Device forEach(int range,  IntConsumer ic) {
       return(forEach(range, .8f, ic));
    }

    public Device forEach(int range, float gpuShare, IntConsumer ic) {
        //  System.out.println("range = "+range);
        // assume range %64 ==null
        if (range %64 != 0){
            throw new IllegalStateException("expecting rnge %64 == 0");
        }
        int rounds = range/64;
        //    System.out.println("rounds = "+rounds);
        rounds = (int)(rounds*gpuShare);
        int lower = rounds*64;
        System.out.println("gpu = "+lower);
         System.out.println("jtp = "+(range-lower));
        Thread t1 = new Thread( () ->  jtpDevice.forEach(lower, range, 2, ic) );
        t1.start();
        hsaDevice.forEach(lower, ic);
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return(this);
    }
}
