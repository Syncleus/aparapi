package com.amd.aparapi.tools;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.ClassModel;
import com.amd.aparapi.MethodModel;
import com.amd.aparapi.InstructionHelper;
import com.amd.aparapi.ClassParseException;

public class javap{
   static public void main(String[] _args) throws ClassNotFoundException, AparapiException{
      ClassModel cm = ClassModel.getClassModel(Class.forName(_args[0]));
      MethodModel mm = cm.getMethodModel(_args[1], _args[2]);
      System.out.println(InstructionHelper.getFoldedView(mm));
   }
}
