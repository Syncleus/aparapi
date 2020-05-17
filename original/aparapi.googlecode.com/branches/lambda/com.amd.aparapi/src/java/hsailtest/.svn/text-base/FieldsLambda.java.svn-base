package hsailtest;

import com.amd.aparapi.*;


public class FieldsLambda {

    int fromId = 0;
    float toId = 0;


    public void test() throws ClassParseException {
        Device.hsa().forEach(30, id -> {
            if (id == 24) {
                fromId = id;
                toId = (float) id;
            }
        });

        System.out.println("fromId = " + fromId);
        System.out.println("toId = " + toId);
    }


    public static void main(String[] args) throws AparapiException {
        (new FieldsLambda()).test();

    }
}
