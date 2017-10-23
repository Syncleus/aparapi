package com.aparapi.opencl.vector;

import com.aparapi.Kernel.OpenCLMappingPattern;

public final class Float2 {

    @OpenCLMappingPattern(mapTo = "(float2)( 0, 0 )")
    public static Float2 create() {
        return create(0f);
    }

    @OpenCLMappingPattern(mapTo = "(float2)( %1$s, %1$s )")
    public static Float2 create(float n) {
        return new Float2(n, n);
    }

    @OpenCLMappingPattern(mapTo = "(float2)( %s, %s )")
    public static Float2 create(float x, float y) {
        return new Float2(x, y);
    }

    public final float x;
    public final float y;

    private Float2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @OpenCLMappingPattern(mapTo = "+")
    public Float2 add(Float2 v1) {
        return Float2.create(this.x + v1.x, this.y + v1.y);
    }

    @OpenCLMappingPattern(mapTo = "-")
    public Float2 subtract(Float2 v1) {
        return Float2.create(this.x - v1.x, this.y - v1.y);
    }

    @OpenCLMappingPattern(mapTo = "*")
    public Float2 multiply(Float2 v1) {
        return Float2.create(this.x * v1.x, this.y * v1.y);
    }

    @OpenCLMappingPattern(mapTo = "/")
    public Float2 divide(Float2 v1) {
        return Float2.create(this.x / v1.x, this.y / v1.y);
    }

    @OpenCLMappingPattern(mapTo = "%")
    public Float2 remainder(Float2 v1) {
        return Float2.create(this.x % v1.x, this.y % v1.y);
    }
}
