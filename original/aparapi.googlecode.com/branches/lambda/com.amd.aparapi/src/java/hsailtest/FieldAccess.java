package hsailtest;

import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class FieldAccess {
    public int value=0;

    public void test(){
        IntConsumer ic = gid -> {
            int local =value;
        };
        Device.hsa().forEach(1, ic);
    }

    public static void main(String[] args) throws Exception {
       FieldAccess fieldAccess = new FieldAccess();
       fieldAccess.test();
    }
}
