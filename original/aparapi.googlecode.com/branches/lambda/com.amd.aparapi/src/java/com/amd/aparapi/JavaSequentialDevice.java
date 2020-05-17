package com.amd.aparapi;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.IntConsumer;

public class JavaSequentialDevice extends Device{


   public Device forEach(int _range, final IntConsumer _intConsumer){

      for(int t = 0; t < _range; t++){
         _intConsumer.accept(t);
      }
      return (this);
   }

}
