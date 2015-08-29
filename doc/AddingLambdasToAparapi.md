#AddingLambdasToAparapi
*Adding Java 8 Lambda Support to Aparapi Updated Jun 24, 2013 by frost.g...@gmail.com*

In the recently added ''lambda'' branch we have been experimenting with adding lambda support to Aparapi. We believe that this upcomming Java 8 feature will be a natural way to express parallel algorithms which can be executed on the GPU.

A link to the branch can be found here preview.

You will need to get the latest binary build of ''Project Lambda'' to experiment with these new features. The 'Project Lambda' preview can be found here.

Once you have a Lambda enabled Java 8 JDK Java set JAVA_HOME to your Java8 Lambda enabled compiler and build Aparapi.

So from the root of SumatraExperiments just use

    $ ant
We are slowly walking through some of the Aparapi demos and converting them. At present NBody and Mandel have been converted.

With Lambda enabled Aparapi we remove the need to derive from a base Kernel class, we will allow the user to express their code as a lambda using the following basic pattern

    Device.bestGPU().forEach(int range, IntConsumer lambda);
The Java 8 stream API defines a type called java.util.function.IntConsumer. This is essentially an interface with a Single Abstract Method (these types are referred to as SAM types in the stream API code).

IntConsumer looks something like....

    interface IntConsumer{
       public void accept(int Id);
    }
So you can run the familiar 'squares' kernel using

    int in[] = ..//
    int out[] = .../
    Device.bestGPU().forEach(in.length, (i)->{
       out[i] = in[i]*in[i];
     });
Instead of

    int in[] = ..//
    int out[] = .../
    Device.bestGPU().forEach(in.length, new IntConsumer(){
       public void accept(int i){
           out[i] = in[i]*in[i];
       }
     });
To accomodate lambda's we created Device.forEach(int range, IntConsumer ic) which converts the bytecode of the ic parameter to OpenCL at runtime. The captured args (in, out and i - in this case) are passed to the GPU and the kernel executed.

During our early experiments we encountered an interesting issue. The new 'lambdafied' javac uses Java 7 method handles and invoke dynamic instructions to dispatch the lambda code. It does this by injecting a call to a MethodHandle factory into the call site. At runtime, this factory creates a synthetic class (to capture call-site args) and passes this to our Device.forEach().

We needed to analyse this synthetically generated class in order to work out which args need to be sent to the GPU. Of course we have a bunch of tools already in Aparapi for analyzing bytecode, but this code expects to find bytecode in class files (either in a Jar or on the disk), we had to find a way to access these classfile bytes to Aparapi.

We have a couple of proposed solutions for solving this. The most promising is to turn the aparapi.dll/aparapi.so native library (used by Aparapi at runtime) into a JVMTI agent (like hprof). JVMTI agents are native libraries which have access to some aspects of a running JVM (via the JVM Tool Interface). We havea prototype JVMTI agent which 'listens' for classfiles which represent these 'synthetic lambda helpers' and allows us to get hold of the bytecode for these classes.

This will mean that in future we will change how Aparapi is launched.

Instead of

    $ java -Djava.library.path=path/to/aparapi -classpath path/to/aparapi/aparapi.jar:your.jar YourClass
We will use

    $ java -agentlib=path/to/aparapi/aparapi.dll -classpath path/to/aparapi/aparapi.jar:your.jar YourClass
We are also looking into the possibility of having this agent provide the bytecode for all Aparapi classes. We believe that this will enable us to ultimately remove MethodModel/ClassModel and even the InstructionSet classes and handling all of this in JNI.

We would welcome comments on these proposals. Either here, or in the discussion list. Let us know what you think.

##Consequences of lambdification of Aparapi.

* No support for local memory, group size or barriers in Lambda form
* Calls to Kernel base class methods (such as getGlobalId()) will not be allowed. The 'global id' will be passed as an arg to the lambda.
* We will need to add support for calling static methods (of course the bytecode for the called methods cannot violate Aparapi restrictions).
* We might need to drop support for multi dimension dispatch. This is more a convergence story with Sumatra (which is unlikely to support this)
* Unlikely that explicit buffer management will be simple.
* We can use lambda's for control as well as the kernel itself. See examples below.

##Alternate forms for kernel dispatch

This version would allow us to carry over Aparapi's device selection

    Device.bestGPU().forEach(1024, i->{lambda});
This version would allow us to carry over Aparapi's Range selection

    Device.bestGPU().range2D(width, height).forEach(1024, rid->{lambda});
This version would allow us to mimic Kernel.execute(1024, 5)

    Device.bestGPU().forEach(1024, 5, (id, passid)->{lambda});
We could even have the range iterated over until some other lambda determines we are done

    Device.bestGPU().forEachUntil(1024, id->{lambda}, ->{predicate lambda});
Explicit buffer handling could be removed in many cases by allowing the bytecode of the 'until' predicate to be snooped for buffer references.

    int lotsOfData[] = ...;
    boolean found[false] = new boolean[1];
    Device.bestGPU().forEachUntil(1024, 5,
       (id, passid)->{ /* mutate lotsOfData, found[0]=true when done */ }
       ->{found[0]]});
In the above cases Aparapi can determine that between each pass it needs to ''ONLY'' copy found[] back from the device.

There is no reason that the range itself needs to be constant, we can use a collection/iterable. This helps with some reductions.

    int range[] = new int[]{1024,512,128,64,32,16,8,4,2,1,0};
    Device.bestGPU().forEach(range,{lambda});
or the range can be a lambda itself, here we specify a start and end value for the range itself, and a lambda to provide each step.

    Device.bestGPU().forEach(1024, 1, r->{return(r/2);},(pass, r, id)->{lambda});
    // or
    Device.bestGPU().forEach(1, 1024, r->{return(r*2);},(pass, r, id)->{lambda});