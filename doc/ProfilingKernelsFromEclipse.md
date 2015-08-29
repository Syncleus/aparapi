#ProfilingKernelsFromEclipse
*Profiling Kernels with AMD profiler in Eclipse (Indigo) Updated May 14, 2012 by frost.g...@gmail.com*

##Profiling Kernels with AMD profiler in Eclipse (Indigo)

Wayne Johnson

12 May 2012
Disclaimer: This has been tested with Eclipse (Indigo SR1) only on W7SR1.

Assume your Eclipse project follows a typical Maven layout:

    Project
       src/main/java/...
         AlgorithmImplementation.java
       src/test/java/...
         BenchmarkRunner.java
         BenchmarkTest.java
       lib/aparapi-2012-02-15/
         aparapi jar file
         native libraries for W7, Linux, and OSX
         …
       profiles/
         [this is where the profiles and logs will be generated]

1. Download and install the current AMD APP SDK
2. Download and install Aparapi (see Wiki), making sure that the native libraries are on your build path.
3. Create your algorithm implementation(s).

        example: AlgorithmImplementations.java

4. Create your performance benchmark test as a JUnit test case to exercise your implementations.

        example: BenchmarkTest.java

5. Test your JUnit test case inside Eclipse using BenchmarkRunner to make sure it works. The runner will be the main application for the runnable jar file you create in the next step.

        This step will also automatically create the launch configuration that the export command will ask you for. Select BenchmarkRunner.java

        Right-click > Run as > Java application

6. Export your project as a runnable jar file.

    Right-click > Export...
      [wizard] Java > Runnable Jar File. Next.
        Launch configuration: BenchmarkRunner [1] - Project
        Export destination: Project\runner.jar
        Library handling: [use default]    Finish.
      Ok on “...repacks referenced libraries”
      Yes on “Confirm replace” [You won’t see this dialog on the first export but will on subsequent exports]
      Ok [ignore warning dialog]

    After refreshing Project, you should see a runner.jar file at the top level.

7. Create an external tool configuration to generate the performance counter profile

    Run > External Tools > External Tool Configurations...
      Name: AMD counters - Project
      Location: C:\Program Files (x86)\AMD APP\tools\AMD APP Profiler 2.4\x64\sprofile.exe
      Arguments:
       -o "${project_loc}\profiles\counters.csv"
       -w "${project_loc}"
       "C:\Program Files\Java\jdk1.6.0_30\bin\java.exe"
       -Djava.library.path="lib\aparapi-2012-02-15"
       -jar "${project_loc}\runner.jar"


    Note: The ''java.library.path'' indicates the relative location of the folder containing the native libraries used by Aparapi. If this is not set correctly, steps 9 and 10 below will run in JTP execution mode and the only error message you will see on the Eclipse console is that the profile was not generated. This is because nothing executed on the GPU.

8. Create an external tool configuration to generate the cltrace and summary profiles.

    1. Run > External Tools > External Tool Configurations...
    2. Name: AMD cltrace - Project
    3. Location: C:\Program Files (x86)\AMD APP\tools\AMD APP Profiler 2.4\x64\sprofile.exe
    4. Arguments:
        -o "${project_loc}\profiles\cltrace.txt" -k all -r -O -t -T
        -w "${project_loc}"
        "C:\Program Files\Java\jdk1.6.0_30\bin\java.exe"
        -Djava.library.path="lib\aparapi-2012-02-15"
        -jar "${project_loc}\runner.jar"


9. Run the AMD profiler counter configuration to generate the counter profile.

     Run > External Tools > AMD counters - Project


10. Run the AMD profiler cltrace configuration to generate the cltrace and summary profiles.

    Run > External Tools > AMD cltrace - Project
    A project file for testing the above instructions can be found http://code.google.com/p/aparapi/source/browse/trunk/wiki-collateral/ProfilingKernelsFormEclipseProject.zip

