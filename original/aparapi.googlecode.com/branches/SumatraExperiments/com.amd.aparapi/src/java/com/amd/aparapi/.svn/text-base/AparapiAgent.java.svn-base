package com.amd.aparapi;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.ClassLoader;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class AparapiAgent {

  static Map<String, byte[]> map = new HashMap<String, byte[]>();

  static class Transformer implements ClassFileTransformer{
     @Override public byte[] transform(ClassLoader loader, String name, Class<?> clazz, ProtectionDomain domain, byte[] bytes){
        if (name != null && name.contains("$$")){
          map.put(name.replace('/','.'), bytes);
          System.out.println("+"+name+" length="+bytes.length);
        }
        return(bytes);
     }
  }
  static void premain(String agentArgs, Instrumentation inst){
    System.out.println("inside premain!");
    inst.addTransformer(new Transformer());
  }

  static public byte[] getBytes(Class<?> clazz){
     byte[] bytes = map.get(clazz.getName());
     if (bytes == null){
         System.out.println("can't get bytes for ="+clazz);
     }else{
         System.out.println("getting bytes for ="+clazz+" size="+bytes.length);
     }
     return(bytes);
  }

}
