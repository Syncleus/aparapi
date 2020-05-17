package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class OopLambda {
    public static class P {
      public int x;
       public int y;
        public int v;
       int xy = 0;

        int getX(){
            return(x);
        }
        int getY(){
            return(y);
        }
       int getXY(){
          return(getX()+getY());
       }


       void setX(int _x){
          x = _x;
       }
       void setY(int _y){
          y = _y;
       }
        void setV(int _v){
            v = _v;
        }
       void clear(){
           x=y=0;
       }


       @Override
        public String toString() {
            return ("(" + x + ", " + y + ", "+ v+")");
        }
    }

    static void dump(String type, P[] points) {
        System.out.print(type + " ->");
        for (int i = 0; i < points.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(points[i]);
        }
        System.out.println();
    }


    public static void main(String[] args) throws AparapiException {
        System.out.println("PATH="+System.getenv("PATH"));
        int len = 12;
        P[] points = new P[len];

        for (int i = 0; i < len; i++) {
            points[i] = new P();
        }



        IntConsumer ic = gid -> {
            P p = points[gid];
            //p.x = gid;
           // p.y = gid*2;
            p.setX(gid);
            p.setY(gid*2);
            p.setV(p.getXY());
           // p.setX(p.getX()+gid);

          //  p.setY(p.getY()+gid * 2);
         //   points[gid].xy=points[gid].getXY();
        };

        Device.hsa().forEach(len, ic);
        dump("hsa", points);
        Device.jtp().forEach(len, i-> points[i].clear());
        Device.jtp().forEach(len, ic);
        dump("jtp", points);
        Device.jtp().forEach(len, i-> points[i].clear());
        Device.seq().forEach(len, ic);
        dump("seq", points);
    }
}
