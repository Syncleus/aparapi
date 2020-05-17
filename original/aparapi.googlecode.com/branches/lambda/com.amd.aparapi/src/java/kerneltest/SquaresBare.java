package kerneltest;

import com.amd.aparapi.*;


public class SquaresBare{


   void run(int[] in, int[] out, int gid){
      out[gid] = in[gid] * in[gid];
   }


   public static void main(String[] args) throws AparapiException{
      ClassModel classModel = ClassModel.getClassModel(SquaresBare.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "([I[II)V");
      method.getInstructions();
      Entrypoint entryPoint = classModel.getKernelEntrypoint("run", "([I[II)V", SquaresBare.class);
      String openCL = null;
      try{
         openCL = OpenCLKernelWriter.writeToString(entryPoint);
         System.out.println(openCL);
      }catch(CodeGenException codeGenException){
         System.out.println(codeGenException);
         codeGenException.printStackTrace();
      }
   }
}
