package com.amd.aparapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ParseJava8{
   public static void main(String[] args) throws FileNotFoundException, AparapiException {
      File file = new File(
            "c:\\Users\\Gary\\aparapi.googlecode.com\\branches\\MockLambda\\wheresmylambdabytecode\\classes\\Main.class");
      ClassModel classModel = new ClassModel(new FileInputStream(file));
      Entrypoint entrypoint = classModel.getEntrypoint("lambda$0", "([I[II)V", null);
      String string = KernelWriter.writeToString(entrypoint);
   }
}

