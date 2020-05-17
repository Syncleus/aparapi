package com.amd.aparapi.examples.nbody;

import java.util.List;

public final class Body{
  protected final float delT = .005f;
  protected final float espSqr = 1.0f;	
   public static List<Body> allBodies; 	
	
   public Body(float _x, float _y, float _z, float _m) {
      x = _x;
      y = _y;
      z = _z;
      m = _m;
   }

   float x, y, z, m, vx, vy, vz;

   public float getX() { return x;  }
   public float getY() { return y;  }
   public float getZ() { return z;  }

   public float getVx() { return vx; }
   public float getVy() { return vy; }
   public float getVz() { return vz; }

   public float getM() { return m; }
   public void setM(float _m) { m = _m; }

   public void setX(float _x) { x = _x; }
   public void setY(float _y) { y = _y; }
   public void setZ(float _z) { z = _z; }
   
   public void setVx(float _vx) { vx = _vx; }
   public void setVy(float _vy) { vy = _vy; }
   public void setVz(float _vz) { vz = _vz; }
   
   float ax, ay, az;
   
   public void setAx(float x) { ax = x; }
   public void setAy(float y) { ax = y; }
   public void setAz(float z) { ax = z; }

   public synchronized void accAx(float x) { ax = ax + x; }
   public synchronized void accAy(float x) { ay = ay + x; }
   public synchronized void accAz(float x) { az = az + x; }
   
   void nextMove() {
	   setAx(0);
	   setAy(0);
	   setAz(0);
	   	   
	   final float myPosx = this.getX();
	   final float myPosy = this.getY();
	   final float myPosz = this.getZ();
	   
	   allBodies.parallel().forEach( b -> {
		   final float dx = b.getX() - myPosx;
		   final float dy = b.getY() - myPosy;
		   final float dz = b.getZ() - myPosz;
		   final float invDist = 1.0f / (float)Math.sqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
		   final float s = b.getM() * invDist * invDist * invDist;
		   accAx(s * dx);
		   accAy(s * dy);
		   accAz(s * dz);
	   } );
	   
	   ax = ax * delT;
	   ay = ay * delT;
	   az = az * delT;
	   this.setX(myPosx + (this.getVx() * delT) + (ax * .5f * delT));
	   this.setY(myPosy + (this.getVy() * delT) + (ay * .5f * delT));
	   this.setZ(myPosz + (this.getVz() * delT) + (az * .5f * delT));

	   this.setVx(this.getVx() + ax);
	   this.setVy(this.getVy() + ay);
	   this.setVz(this.getVz() + az);
   }
   
}

/*
public void run() {
final int body = getGlobalId();
final int count = getGlobalSize(0);
final int globalId = body;

float accx = 0.f;
float accy = 0.f;
float accz = 0.f;

final float myPosx = bodies[body].getX();
final float myPosy = bodies[body].getY();
final float myPosz = bodies[body].getZ();
for (int i = 0; i < count; i ++) {
  final float dx = bodies[i].getX() - myPosx;
  final float dy = bodies[i].getY() - myPosy;
  final float dz = bodies[i].getZ() - myPosz;
  final float invDist = rsqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
  final float s = mass * invDist * invDist * invDist;
  accx = accx + (s * dx);
  accy = accy + (s * dy);
  accz = accz + (s * dz);
}
accx = accx * delT;
accy = accy * delT;
accz = accz * delT;
bodies[body].setX(myPosx + (bodies[body].getVx() * delT) + (accx * .5f * delT));
bodies[body].setY(myPosy + (bodies[body].getVy() * delT) + (accy * .5f * delT));
bodies[body].setZ(myPosz + (bodies[body].getVz() * delT) + (accz * .5f * delT));

bodies[body].setVx(bodies[body].getVx() + accx);
bodies[body].setVy(bodies[body].getVy() + accy);
bodies[body].setVz(bodies[body].getVz() + accz);
}
*/