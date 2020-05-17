package com.amd.aparapi;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

public class HSADevice extends Device {
    // TODO: We need to cache somewhere to avoid creating HSAIl each time
    static class CachedRunner {
        String hsail;
        Object instance;

        OkraRunner runner;
        boolean isStatic;
        Field[] capturedFields;
        Object[] args;
    }

    static Map<Class<? extends IntConsumer>, CachedRunner> map = new HashMap<Class<? extends IntConsumer>, CachedRunner>();

    public void dump(IntConsumer ic) {
        try{
        LambdaKernelCall lkc = new LambdaKernelCall(ic);

        ClassModel classModel = ClassModel.getClassModel(lkc.getLambdaKernelClass());

        ClassModel.ClassModelMethod method = classModel.getMethod(lkc.getLambdaMethodName(), lkc.getLambdaMethodSignature());


        HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);

        HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method);


        hsailMethod.render(renderer);
        System.out.println(renderer.toString());
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        } catch (ClassParseException e) {
            e.printStackTrace();
        } catch (AparapiException e) {
            e.printStackTrace();
        }
    }
    public Device forEach(int size, String _hsailText, IntConsumer ic) {
        try {
            CachedRunner cachedRunner = null;
            if (map.containsKey(ic.getClass())) {
                cachedRunner = map.get(ic.getClass());
            } else {
                cachedRunner = new CachedRunner();
                LambdaKernelCall lkc = new LambdaKernelCall(ic);
               // System.out.println("class="+lkc.getLambdaKernelClass());
                ClassModel classModel = ClassModel.getClassModel(lkc.getLambdaKernelClass());
               // System.out.println("methodname="+lkc.getLambdaMethodName());
               // System.out.println("methodsig="+lkc.getLambdaMethodSignature());
               // ClassModel.ClassModelMethod method = classModel.getMethod(lkc.getLambdaMethodName(), lkc.getLambdaMethodSignature());


                HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);

             //   HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method);


              //  hsailMethod.render(renderer);
                cachedRunner.hsail = _hsailText;
                System.out.println(cachedRunner.hsail);
                cachedRunner.runner = new OkraRunner(cachedRunner.hsail);
                cachedRunner.isStatic = lkc.isStatic();
                if (!cachedRunner.isStatic) {
                    cachedRunner.instance = lkc.getLambdaKernelThis();
                }
                cachedRunner.capturedFields= lkc.getLambdaCapturedFields();
                cachedRunner.args = new Object[cachedRunner.capturedFields.length+(cachedRunner.isStatic?0:1)+1];


                map.put(ic.getClass(), cachedRunner);
            }
            int arg=0;
            if (!cachedRunner.isStatic){
                cachedRunner.args[arg++]=cachedRunner.instance;
            }
            try {
                for (Field f : cachedRunner.capturedFields) {
                    f.setAccessible(true);
                    //  String name = f.getName();
                    Type type = f.getType();
                    if (type.equals(float.class)) {
                        cachedRunner.args[arg++]= f.getFloat(ic);
                    } else if (type.equals(int.class)) {
                        cachedRunner.args[arg++]=f.getInt(ic);
                    } else {
                        cachedRunner.args[arg++]=f.get(ic);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            cachedRunner.args[arg++]=0;
            cachedRunner.runner.run(size, cachedRunner.args);


        } catch (AparapiException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return(this);

    }
    public Device forEach(int size, IntConsumer ic) {
        try {
            CachedRunner cachedRunner = null;
            if (map.containsKey(ic.getClass())) {
                cachedRunner = map.get(ic.getClass());
            } else {
                cachedRunner = new CachedRunner();
                LambdaKernelCall lkc = new LambdaKernelCall(ic);
                System.out.println("class="+lkc.getLambdaKernelClass());
                ClassModel classModel = ClassModel.getClassModel(lkc.getLambdaKernelClass());
                System.out.println("methodname="+lkc.getLambdaMethodName());
                System.out.println("methodsig="+lkc.getLambdaMethodSignature());
                ClassModel.ClassModelMethod method = classModel.getMethod(lkc.getLambdaMethodName(), lkc.getLambdaMethodSignature());


                HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);

                HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method);


                hsailMethod.render(renderer);
                cachedRunner.hsail = renderer.toString();
                System.out.println(cachedRunner.hsail);
                cachedRunner.runner = new OkraRunner(cachedRunner.hsail);
                cachedRunner.isStatic = lkc.isStatic();
                if (!cachedRunner.isStatic) {
                    cachedRunner.instance = lkc.getLambdaKernelThis();
                }
                cachedRunner.capturedFields= lkc.getLambdaCapturedFields();
                cachedRunner.args = new Object[cachedRunner.capturedFields.length+(cachedRunner.isStatic?0:1)+1];


                map.put(ic.getClass(), cachedRunner);
            }
            int arg=0;
            if (!cachedRunner.isStatic){
                cachedRunner.args[arg++]=cachedRunner.instance;
            }
            try {
                for (Field f : cachedRunner.capturedFields) {
                    f.setAccessible(true);
                  //  String name = f.getName();
                    Type type = f.getType();
                    if (type.equals(float.class)) {
                        cachedRunner.args[arg++]= f.getFloat(ic);
                    } else if (type.equals(int.class)) {
                        cachedRunner.args[arg++]=f.getInt(ic);
                    } else {
                        cachedRunner.args[arg++]=f.get(ic);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            cachedRunner.args[arg++]=0;
            cachedRunner.runner.run(size, cachedRunner.args);


        } catch (AparapiException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return (this);

    }
}
