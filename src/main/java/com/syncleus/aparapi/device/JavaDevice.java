package com.syncleus.aparapi.device;

public class JavaDevice extends Device {

   public static final JavaDevice THREAD_POOL = new JavaDevice(TYPE.JTP, "Java Thread Pool", -3);
   public static final JavaDevice ALTERNATIVE_ALGORITHM = new JavaDevice(TYPE.ALT, "Java Alternative Algorithm", -2);
   public static final JavaDevice SEQUENTIAL = new JavaDevice(TYPE.SEQ, "Java Sequential", -1);

   private final String name;
   private final long deviceId;

   private JavaDevice(TYPE _type, String _name, long deviceId) {
      this.deviceId = deviceId;
      this.type = _type;
      this.name = _name;
   }

   @Override
   public String getShortDescription() {
      return name;
   }

   @Override
   public long getDeviceId() {
      return deviceId;
   }

   @Override
   public String toString() {
      return getShortDescription();
   }
}
