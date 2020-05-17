package kerneltest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.ClassModel;
import com.amd.aparapi.Kernel;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 5/8/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReturnArray extends Kernel{

   float[] returnFloatArrayVar(){
      float[] floats = new float[1024];
      return floats;
   }

   @Override
   public void run(){

      returnFloatArrayVar();
   }

   public static void main(String[] args) throws AparapiException{
      ReturnArray ra = new ReturnArray();
      ra.execute(1);
   }
}

