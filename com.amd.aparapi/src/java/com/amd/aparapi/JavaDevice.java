package com.amd.aparapi;


public class JavaDevice implements Device{

   private TYPE type = TYPE.UNKNOWN;

   private int maxWorkGroupSize;

   private int maxWorkItemDimensions;

   private int[] maxWorkItemSize = new int[] {
         0,
         0,
         0
   };

   public TYPE getType() {
      return type;
   }

   public void setType(TYPE type) {
      this.type = type;
   }

   public int getMaxWorkItemDimensions() {
      return maxWorkItemDimensions;
   }

   public void setMaxWorkItemDimensions(int _maxWorkItemDimensions) {
      maxWorkItemDimensions = _maxWorkItemDimensions;
   }

   public int getMaxWorkGroupSize() {
      return maxWorkGroupSize;
   }

   public void setMaxWorkGroupSize(int _maxWorkGroupSize) {
      maxWorkGroupSize = _maxWorkGroupSize;
   }

   public int[] getMaxWorkItemSize() {
      return maxWorkItemSize;
   }

   public void setMaxWorkItemSize(int[] maxWorkItemSize) {
      this.maxWorkItemSize = maxWorkItemSize;
   }

   public String toString() {
      StringBuilder s = new StringBuilder("{");
      boolean first = true;
      for (int workItemSize : maxWorkItemSize) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }
         s.append(workItemSize);
      }
      s.append("}");
      return ("Type:" + type + "\n  maxWorkItemDimensions=" + maxWorkItemDimensions + "\n  maxWorkItemSizes=" + s
            + "\n  maxWorkWorkGroupSize=" + maxWorkGroupSize);
   }

   void setMaxWorkItemSize(int _dim, int _value) {
      maxWorkItemSize[_dim] = _value;
   }

   public Range createRange(int _globalWidth) {
      return (Range.create(this, _globalWidth));
   }

   public Range createRange(int _globalWidth, int _localWidth) {
      return (Range.create(this, _globalWidth, _localWidth));
   }

   public Range createRange2D(int _globalWidth, int _globalHeight) {
      return (Range.create2D(this, _globalWidth, _globalHeight));
   }

   public Range createRange2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      return (Range.create2D(this, _globalWidth, _globalHeight, _localWidth, _localHeight));
   }

   public Range createRange3D(int _globalWidth, int _globalHeight, int _globalDepth) {
      return (Range.create3D(this, _globalWidth, _globalHeight, _globalDepth));
   }

   public Range createRange3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight,
         int _localDepth) {
      return (Range.create3D(this, _globalWidth, _globalHeight, _globalDepth, _localWidth, _localHeight, _localDepth));
   }

}
