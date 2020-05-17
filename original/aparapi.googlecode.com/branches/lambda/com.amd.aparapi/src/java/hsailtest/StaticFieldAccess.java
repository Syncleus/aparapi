package hsailtest;

import com.amd.aparapi.Device;
import com.amd.aparapi.UnsafeWrapper;

import java.lang.reflect.Field;
import java.util.function.IntConsumer;


public class StaticFieldAccess {
    public static int value=4;

    public void test(){
        IntConsumer ic = gid -> {
            int local =value;
        };
        Device.hsa().forEach(1, ic);
    }

    public static void main(String[] args) throws Exception {

        StaticFieldAccess fieldAccess = new StaticFieldAccess();

       //(new FieldAccess()).test();
        Field field = StaticFieldAccess.class.getField("value");
        long offset = UnsafeWrapper.objectFieldOffset(field);
        int addressSize = UnsafeWrapper.addressSize();
        long address = UnsafeWrapper.addressOf(fieldAccess);
        int local_value = UnsafeWrapper.getUnsafe().getInt(address + offset);
        System.out.printf("     offset = %x\n",offset);
        System.out.printf("addressSize = %x\n", addressSize);
        System.out.printf("    address = %x\n",address);
        System.out.printf("local_value = %x\n",local_value);
        UnsafeWrapper.getUnsafe().putInt(address + offset, 8);
        System.out.printf("value = %x\n",fieldAccess.value);
        fieldAccess.test();

    }
}
