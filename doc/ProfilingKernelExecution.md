#ProfilingKernelExecution
*Using Aparapi's built in profiling APIs Updated May 7, 2013 by frost.g...@gmail.com*

If you want to extract OpenCL performance info from a kernel at runtime you need to set the property :-

    -Dcom.amd.aparapi.enableProfiling=true

Your application can then call kernel.getProfileInfo() after a successful call to kernel.execute(range) to extract a List List<ProfileInfo>.

Each ProfileInfo holds timing information for buffer writes, executs and buffer reads.

The following code will print a simple table of profile information

    List<ProfileInfo> profileInfo = k.getProfileInfo();
    for (final ProfileInfo p : profileInfo) {
       System.out.print(" " + p.getType() + " " + p.getLabel() + " " + (p.getStart() / 1000) + " .. "
           + (p.getEnd() / 1000) + " " + ((p.getEnd() - p.getStart()) / 1000) + "us");
       System.out.println();
    }

Here is an example implementation

            final float result[] = new float[2048*2048];
            Kernel k = new Kernel(){
               public void run(){
                  final int gid=getGlobalId();
                  result[gid] =0f;
               }
            };
            k.execute(result.length);
            List<ProfileInfo> profileInfo = k.getProfileInfo();

            for (final ProfileInfo p : profileInfo) {
               System.out.print(" " + p.getType() + " " + p.getLabel() + " " + (p.getStart() / 1000) + " .. "
                  + (p.getEnd() / 1000) + " " + ((p.getEnd() - p.getStart()) / 1000) + "us");
               System.out.println();
            }
            k.dispose();
        }
    }
And here is the tabular output from

        java
           -Djava.library.path=${APARAPI_HOME}
           -Dcom.amd.aparapi.enableProfiling=true
           -cp ${APARAPI_HOME}:.
           MyClass

      W val$result 69500 .. 72694 3194us
      X exec()     72694 .. 72835  141us
      R val$result 75327 .. 78225 2898us

The table shows that the transfer of the 'result' buffer to the device ('W') took 3194 us (micro seconds), the execute ('X') of the kernel 141 us and the read ('R') of resulting buffer 2898 us.