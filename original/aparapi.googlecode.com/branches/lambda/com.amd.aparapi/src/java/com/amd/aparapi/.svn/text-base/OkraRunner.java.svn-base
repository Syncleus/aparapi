package com.amd.aparapi;

import com.amd.okra.OkraContext;
import com.amd.okra.OkraKernel;
import java.util.function.IntConsumer;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 5/22/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class OkraRunner{
    OkraContext context;
    OkraKernel k;
    OkraRunner(String _hsail){
        context = new OkraContext();
        k = new OkraKernel(context, _hsail, "&run");
    }
   public void run(int _size, Object... args){
      k.setLaunchAttributes(_size, 0);
    //  if (Boolean.getBoolean("dispatch")){
        k.dispatchWithArgs(args);
   //   }else{
     //     System.out.println("-Ddispath=true if you want to actually dispatch");
     // }
   }


}
