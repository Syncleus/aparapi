/* ============================================================

Copyright (c) 2009-2010 Advanced Micro Devices, Inc.  All rights reserved.
 
Redistribution and use of this material is permitted under the following 
conditions:
 
Redistributions must retain the above copyright notice and all terms of this 
license.
 
In no event shall anyone redistributing or accessing or using this material 
commence or participate in any arbitration or legal action relating to this 
material against Advanced Micro Devices, Inc. or any copyright holders or 
contributors. The foregoing shall survive any expiration or termination of 
this license or any agreement or access or use related to this material. 

ANY BREACH OF ANY TERM OF THIS LICENSE SHALL RESULT IN THE IMMEDIATE REVOCATION 
OF ALL RIGHTS TO REDISTRIBUTE, ACCESS OR USE THIS MATERIAL.

THIS MATERIAL IS PROVIDED BY ADVANCED MICRO DEVICES, INC. AND ANY COPYRIGHT 
HOLDERS AND CONTRIBUTORS "AS IS" IN ITS CURRENT CONDITION AND WITHOUT ANY 
REPRESENTATIONS, GUARANTEE, OR WARRANTY OF ANY KIND OR IN ANY WAY RELATED TO 
SUPPORT, INDEMNITY, ERROR FREE OR UNINTERRUPTED OPERA TION, OR THAT IT IS FREE 
FROM DEFECTS OR VIRUSES.  ALL OBLIGATIONS ARE HEREBY DISCLAIMED - WHETHER 
EXPRESS, IMPLIED, OR STATUTORY - INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED 
WARRANTIES OF TITLE, MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, 
ACCURACY, COMPLETENESS, OPERABILITY, QUALITY OF SERVICE, OR NON-INFRINGEMENT. 
IN NO EVENT SHALL ADVANCED MICRO DEVICES, INC. OR ANY COPYRIGHT HOLDERS OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, REVENUE, DATA, OR PROFITS; OR 
BUSINESS INTERRUPTION) HOWEVER CAUSED OR BASED ON ANY THEORY OF LIABILITY 
ARISING IN ANY WAY RELATED TO THIS MATERIAL, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE. THE ENTIRE AND AGGREGATE LIABILITY OF ADVANCED MICRO DEVICES, 
INC. AND ANY COPYRIGHT HOLDERS AND CONTRIBUTORS SHALL NOT EXCEED TEN DOLLARS 
(US $10.00). ANYONE REDISTRIBUTING OR ACCESSING OR USING THIS MATERIAL ACCEPTS 
THIS ALLOCATION OF RISK AND AGREES TO RELEASE ADVANCED MICRO DEVICES, INC. AND 
ANY COPYRIGHT HOLDERS AND CONTRIBUTORS FROM ANY AND ALL LIABILITIES, 
OBLIGATIONS, CLAIMS, OR DEMANDS IN EXCESS OF TEN DOLLARS (US $10.00). THE 
FOREGOING ARE ESSENTIAL TERMS OF THIS LICENSE AND, IF ANY OF THESE TERMS ARE 
CONSTRUED AS UNENFORCEABLE, FAIL IN ESSENTIAL PURPOSE, OR BECOME VOID OR 
DETRIMENTAL TO ADVANCED MICRO DEVICES, INC. OR ANY COPYRIGHT HOLDERS OR 
CONTRIBUTORS FOR ANY REASON, THEN ALL RIGHTS TO REDISTRIBUTE, ACCESS OR USE 
THIS MATERIAL SHALL TERMINATE IMMEDIATELY. MOREOVER, THE FOREGOING SHALL 
SURVIVE ANY EXPIRATION OR TERMINATION OF THIS LICENSE OR ANY AGREEMENT OR 
ACCESS OR USE RELATED TO THIS MATERIAL.

NOTICE IS HEREBY PROVIDED, AND BY REDISTRIBUTING OR ACCESSING OR USING THIS 
MATERIAL SUCH NOTICE IS ACKNOWLEDGED, THAT THIS MATERIAL MAY BE SUBJECT TO 
RESTRICTIONS UNDER THE LAWS AND REGULATIONS OF THE UNITED STATES OR OTHER 
COUNTRIES, WHICH INCLUDE BUT ARE NOT LIMITED TO, U.S. EXPORT CONTROL LAWS SUCH 
AS THE EXPORT ADMINISTRATION REGULATIONS AND NATIONAL SECURITY CONTROLS AS 
DEFINED THEREUNDER, AS WELL AS STATE DEPARTMENT CONTROLS UNDER THE U.S. 
MUNITIONS LIST. THIS MATERIAL MAY NOT BE USED, RELEASED, TRANSFERRED, IMPORTED,
EXPORTED AND/OR RE-EXPORTED IN ANY MANNER PROHIBITED UNDER ANY APPLICABLE LAWS, 
INCLUDING U.S. EXPORT CONTROL LAWS REGARDING SPECIFICALLY DESIGNATED PERSONS, 
COUNTRIES AND NATIONALS OF COUNTRIES SUBJECT TO NATIONAL SECURITY CONTROLS. 
MOREOVER, THE FOREGOING SHALL SURVIVE ANY EXPIRATION OR TERMINATION OF ANY 
LICENSE OR AGREEMENT OR ACCESS OR USE RELATED TO THIS MATERIAL.

NOTICE REGARDING THE U.S. GOVERNMENT AND DOD AGENCIES: This material is 
provided with "RESTRICTED RIGHTS" and/or "LIMITED RIGHTS" as applicable to 
computer software and technical data, respectively. Use, duplication, 
distribution or disclosure by the U.S. Government and/or DOD agencies is 
subject to the full extent of restrictions in all applicable regulations, 
including those found at FAR52.227 and DFARS252.227 et seq. and any successor 
regulations thereof. Use of this material by the U.S. Government and/or DOD 
agencies is acknowledgment of the proprietary rights of any copyright holders 
and contributors, including those of Advanced Micro Devices, Inc., as well as 
the provisions of FAR52.227-14 through 23 regarding privately developed and/or 
commercial computer software.

This license forms the entire agreement regarding the subject matter hereof and 
supersedes all proposals and prior discussions and writings between the parties 
with respect thereto. This license does not affect any ownership, rights, title,
or interest in, or relating to, this material. No terms of this license can be 
modified or waived, and no breach of this license can be excused, unless done 
so in a writing signed by all affected parties. Each term of this license is 
separately enforceable. If any term of this license is determined to be or 
becomes unenforceable or illegal, such term shall be reformed to the minimum 
extent necessary in order for this license to remain in effect in accordance 
with its terms as modified by such reformation. This license shall be governed 
by and construed in accordance with the laws of the State of Texas without 
regard to rules on conflicts of law of any state or jurisdiction or the United 
Nations Convention on the International Sale of Goods. All disputes arising out 
of this license shall be subject to the jurisdiction of the federal and state 
courts in Austin, Texas, and all defenses are hereby waived concerning personal 
jurisdiction and venue of these courts.

============================================================ */


// This is 2 PI / 1024
#define ANGLE 0x1.921fb6p-8F

// Return sin and cos of -2*pi*i/1024
__attribute__((always_inline)) float
k_sincos(int i, float *cretp)
{
    if (i > 512)
	i -= 1024;

    float x = i * -ANGLE;
    *cretp = native_cos(x);
    return native_sin(x);
}

__attribute__((always_inline)) float4
k_sincos4(int4 i, float4 *cretp)
{
    i -= (i > 512) & 1024;
    float4 x = convert_float4(i) * -ANGLE;
    *cretp = native_cos(x);
    return native_sin(x);
}

// Twiddle factor stuff
#define TWGEN(I,C,S) \
    float C; \
    float S = k_sincos(tbase * I, &C)

#define TW4GEN(I,C,S) \
    float4 C; \
    float4 S = k_sincos4(tbase * I, &C)

#define TWAPPLY(ZR, ZI, C, S) \
    do { \
	float4 __r = C * ZR - S * ZI; \
	ZI = C * ZI + S * ZR; \
	ZR = __r; \
    } while (0)

# define TW4IDDLE4() \
    do { \
        TW4GEN(1, c1, s1); \
        TWAPPLY(zr1, zi1, c1, s1); \
        TW4GEN(2, c2, s2); \
        TWAPPLY(zr2, zi2, c2, s2); \
        TW4GEN(3, c3, s3); \
        TWAPPLY(zr3, zi3, c3, s3); \
    } while (0)
    
# define TWIDDLE4() \
    do { \
        TWGEN(1, c1, s1); \
        TWAPPLY(zr1, zi1, c1, s1); \
        TWGEN(2, c2, s2); \
        TWAPPLY(zr2, zi2, c2, s2); \
        TWGEN(3, c3, s3); \
        TWAPPLY(zr3, zi3, c3, s3); \
    } while (0)

// 4 point FFT
#define FFT4() \
    do { \
        float4 ar0 = zr0 + zr2; \
        float4 ar2 = zr1 + zr3; \
        float4 br0 = ar0 + ar2; \
        float4 br1 = zr0 - zr2; \
        float4 br2 = ar0 - ar2; \
        float4 br3 = zr1 - zr3; \
        float4 ai0 = zi0 + zi2; \
        float4 ai2 = zi1 + zi3; \
        float4 bi0 = ai0 + ai2; \
        float4 bi1 = zi0 - zi2; \
        float4 bi2 = ai0 - ai2; \
        float4 bi3 = zi1 - zi3; \
        zr0 = br0; \
        zi0 = bi0; \
        zr1 = br1 + bi3; \
        zi1 = bi1 - br3; \
        zr3 = br1 - bi3; \
        zi3 = br3 + bi1; \
        zr2 = br2; \
        zi2 = bi2; \
    } while (0)

// First pass of 1K FFT
__attribute__((always_inline)) void
kfft_pass1(uint me,
	    const __global float *gr, const __global float *gi,
	    __local float *lds)
{
    const __global float4 *gp;
    __local float *lp;

    // Pull in transform data
    gp = (const __global float4 *)(gr + (me << 2));
    float4 zr0 = gp[0*64];
    float4 zr1 = gp[1*64];
    float4 zr2 = gp[2*64];
    float4 zr3 = gp[3*64];

    gp = (const __global float4 *)(gi + (me << 2));
    float4 zi0 = gp[0*64];
    float4 zi1 = gp[1*64];
    float4 zi2 = gp[2*64];
    float4 zi3 = gp[3*64];

    FFT4();

    int4 tbase = (int)(me << 2) + (int4)(0, 1, 2, 3);
    TW4IDDLE4();

    // Save registers
    // Note that this pointer is not aligned enough to be cast to a float4*
    lp = lds + ((me << 2) + (me >> 3));

    lp[0] = zr0.x;
    lp[1] = zr0.y;
    lp[2] = zr0.z;
    lp[3] = zr0.w;
    lp += 66*4;

    lp[0] = zr1.x;
    lp[1] = zr1.y;
    lp[2] = zr1.z;
    lp[3] = zr1.w;
    lp += 66*4;

    lp[0] = zr2.x;
    lp[1] = zr2.y;
    lp[2] = zr2.z;
    lp[3] = zr2.w;
    lp += 66*4;

    lp[0] = zr3.x;
    lp[1] = zr3.y;
    lp[2] = zr3.z;
    lp[3] = zr3.w;
    lp += 66*4;

    // Imaginary part
    lp[0] = zi0.x;
    lp[1] = zi0.y;
    lp[2] = zi0.z;
    lp[3] = zi0.w;
    lp += 66*4;

    lp[0] = zi1.x;
    lp[1] = zi1.y;
    lp[2] = zi1.z;
    lp[3] = zi1.w;
    lp += 66*4;

    lp[0] = zi2.x;
    lp[1] = zi2.y;
    lp[2] = zi2.z;
    lp[3] = zi2.w;
    lp += 66*4;

    lp[0] = zi3.x;
    lp[1] = zi3.y;
    lp[2] = zi3.z;
    lp[3] = zi3.w;

    barrier(CLK_LOCAL_MEM_FENCE);
}

// Second pass of 1K FFT
__attribute__((always_inline)) void
kfft_pass2(uint me, __local float *lds)
{
    __local float *lp;

    // Load registers
    lp = lds + (me + (me >> 5));

    float4 zr0, zr1, zr2, zr3;

    zr0.x = lp[0*66];
    zr1.x = lp[1*66];
    zr2.x = lp[2*66];
    zr3.x = lp[3*66];
    lp += 66*4;

    zr0.y = lp[0*66];
    zr1.y = lp[1*66];
    zr2.y = lp[2*66];
    zr3.y = lp[3*66];
    lp += 66*4;

    zr0.z = lp[0*66];
    zr1.z = lp[1*66];
    zr2.z = lp[2*66];
    zr3.z = lp[3*66];
    lp += 66*4;

    zr0.w = lp[0*66];
    zr1.w = lp[1*66];
    zr2.w = lp[2*66];
    zr3.w = lp[3*66];
    lp += 66*4;

    float4 zi0, zi1, zi2, zi3;

    zi0.x = lp[0*66];
    zi1.x = lp[1*66];
    zi2.x = lp[2*66];
    zi3.x = lp[3*66];
    lp += 66*4;

    zi0.y = lp[0*66];
    zi1.y = lp[1*66];
    zi2.y = lp[2*66];
    zi3.y = lp[3*66];
    lp += 66*4;

    zi0.z = lp[0*66];
    zi1.z = lp[1*66];
    zi2.z = lp[2*66];
    zi3.z = lp[3*66];
    lp += 66*4;

    zi0.w = lp[0*66];
    zi1.w = lp[1*66];
    zi2.w = lp[2*66];
    zi3.w = lp[3*66];

    // Transform and twiddle
    FFT4();

    int tbase = (int)(me << 2);
    TWIDDLE4();

    barrier(CLK_LOCAL_MEM_FENCE);

    // Store registers
    lp = lds + ((me << 2) + (me >> 3));

    lp[0] = zr0.x;
    lp[1] = zr1.x;
    lp[2] = zr2.x;
    lp[3] = zr3.x;
    lp += 66*4;

    lp[0] = zr0.y;
    lp[1] = zr1.y;
    lp[2] = zr2.y;
    lp[3] = zr3.y;
    lp += 66*4;

    lp[0] = zr0.z;
    lp[1] = zr1.z;
    lp[2] = zr2.z;
    lp[3] = zr3.z;
    lp += 66*4;

    lp[0] = zr0.w;
    lp[1] = zr1.w;
    lp[2] = zr2.w;
    lp[3] = zr3.w;
    lp += 66*4;

    // Imaginary part
    lp[0] = zi0.x;
    lp[1] = zi1.x;
    lp[2] = zi2.x;
    lp[3] = zi3.x;
    lp += 66*4;

    lp[0] = zi0.y;
    lp[1] = zi1.y;
    lp[2] = zi2.y;
    lp[3] = zi3.y;
    lp += 66*4;

    lp[0] = zi0.z;
    lp[1] = zi1.z;
    lp[2] = zi2.z;
    lp[3] = zi3.z;
    lp += 66*4;

    lp[0] = zi0.w;
    lp[1] = zi1.w;
    lp[2] = zi2.w;
    lp[3] = zi3.w;

    barrier(CLK_LOCAL_MEM_FENCE);
}

// Third pass of 1K FFT
__attribute__((always_inline)) void
kfft_pass3(uint me, __local float *lds)
{
    __local float *lp;

    // Load registers
    lp = lds + (me + (me >> 5));

    float4 zr0, zr1, zr2, zr3;

    zr0.x = lp[0*66];
    zr1.x = lp[1*66];
    zr2.x = lp[2*66];
    zr3.x = lp[3*66];
    lp += 66*4;

    zr0.y = lp[0*66];
    zr1.y = lp[1*66];
    zr2.y = lp[2*66];
    zr3.y = lp[3*66];
    lp += 66*4;

    zr0.z = lp[0*66];
    zr1.z = lp[1*66];
    zr2.z = lp[2*66];
    zr3.z = lp[3*66];
    lp += 66*4;

    zr0.w = lp[0*66];
    zr1.w = lp[1*66];
    zr2.w = lp[2*66];
    zr3.w = lp[3*66];
    lp += 66*4;

    float4 zi0, zi1, zi2, zi3;

    zi0.x = lp[0*66];
    zi1.x = lp[1*66];
    zi2.x = lp[2*66];
    zi3.x = lp[3*66];
    lp += 66*4;

    zi0.y = lp[0*66];
    zi1.y = lp[1*66];
    zi2.y = lp[2*66];
    zi3.y = lp[3*66];
    lp += 66*4;

    zi0.z = lp[0*66];
    zi1.z = lp[1*66];
    zi2.z = lp[2*66];
    zi3.z = lp[3*66];
    lp += 66*4;

    zi0.w = lp[0*66];
    zi1.w = lp[1*66];
    zi2.w = lp[2*66];
    zi3.w = lp[3*66];

    // Transform and twiddle
    FFT4();

    int tbase = (int)((me >> 2) << 4);
    TWIDDLE4();

    barrier(CLK_LOCAL_MEM_FENCE);

    // Save registers
    lp = lds + me;

    lp[0*66] = zr0.x;
    lp[1*66] = zr0.y;
    lp[2*66] = zr0.z;
    lp[3*66] = zr0.w;
    lp += 66*4;

    lp[0*66] = zr1.x;
    lp[1*66] = zr1.y;
    lp[2*66] = zr1.z;
    lp[3*66] = zr1.w;
    lp += 66*4;

    lp[0*66] = zr2.x;
    lp[1*66] = zr2.y;
    lp[2*66] = zr2.z;
    lp[3*66] = zr2.w;
    lp += 66*4;

    lp[0*66] = zr3.x;
    lp[1*66] = zr3.y;
    lp[2*66] = zr3.z;
    lp[3*66] = zr3.w;
    lp += 66*4;

    // Imaginary part
    lp[0*66] = zi0.x;
    lp[1*66] = zi0.y;
    lp[2*66] = zi0.z;
    lp[3*66] = zi0.w;
    lp += 66*4;

    lp[0*66] = zi1.x;
    lp[1*66] = zi1.y;
    lp[2*66] = zi1.z;
    lp[3*66] = zi1.w;
    lp += 66*4;

    lp[0*66] = zi2.x;
    lp[1*66] = zi2.y;
    lp[2*66] = zi2.z;
    lp[3*66] = zi2.w;
    lp += 66*4;

    lp[0*66] = zi3.x;
    lp[1*66] = zi3.y;
    lp[2*66] = zi3.z;
    lp[3*66] = zi3.w;

    barrier(CLK_LOCAL_MEM_FENCE);
}

// Fourth pass of 1K FFT
__attribute__((always_inline)) void
kfft_pass4(uint me, __local float *lds)
{
    __local float *lp;

    // Load registers
    lp = lds + ((me & 0x3) + ((me >> 2) & 0x3)*(66*4) + ((me >> 4) << 2));

    float4 zr0, zr1, zr2, zr3;

    zr0.x = lp[0*66];
    zr0.y = lp[1*66];
    zr0.z = lp[2*66];
    zr0.w = lp[3*66];
    lp += 16;

    zr1.x = lp[0*66];
    zr1.y = lp[1*66];
    zr1.z = lp[2*66];
    zr1.w = lp[3*66];
    lp += 16;

    zr2.x = lp[0*66];
    zr2.y = lp[1*66];
    zr2.z = lp[2*66];
    zr2.w = lp[3*66];
    lp += 16;

    zr3.x = lp[0*66];
    zr3.y = lp[1*66];
    zr3.z = lp[2*66];
    zr3.w = lp[3*66];
    lp += 66*4*4 - 3*16;

    float4 zi0, zi1, zi2, zi3;

    zi0.x = lp[0*66];
    zi0.y = lp[1*66];
    zi0.z = lp[2*66];
    zi0.w = lp[3*66];
    lp += 16;

    zi1.x = lp[0*66];
    zi1.y = lp[1*66];
    zi1.z = lp[2*66];
    zi1.w = lp[3*66];
    lp += 16;

    zi2.x = lp[0*66];
    zi2.y = lp[1*66];
    zi2.z = lp[2*66];
    zi2.w = lp[3*66];
    lp += 16;

    zi3.x = lp[0*66];
    zi3.y = lp[1*66];
    zi3.z = lp[2*66];
    zi3.w = lp[3*66];

    // Transform and twiddle
    FFT4();

    int tbase = (int)((me >> 4) << 6);
    TWIDDLE4();

    barrier(CLK_LOCAL_MEM_FENCE);

    // Save registers in conflict free manner
    lp = lds + me;

    lp[0*68] = zr0.x;
    lp[1*68] = zr0.y;
    lp[2*68] = zr0.z;
    lp[3*68] = zr0.w;
    lp += 68*4;

    lp[0*68] = zr1.x;
    lp[1*68] = zr1.y;
    lp[2*68] = zr1.z;
    lp[3*68] = zr1.w;
    lp += 68*4;

    lp[0*68] = zr2.x;
    lp[1*68] = zr2.y;
    lp[2*68] = zr2.z;
    lp[3*68] = zr2.w;
    lp += 68*4;

    lp[0*68] = zr3.x;
    lp[1*68] = zr3.y;
    lp[2*68] = zr3.z;
    lp[3*68] = zr3.w;
    lp += 68*4;

    // Imaginary part
    lp[0*68] = zi0.x;
    lp[1*68] = zi0.y;
    lp[2*68] = zi0.z;
    lp[3*68] = zi0.w;
    lp += 68*4;

    lp[0*68] = zi1.x;
    lp[1*68] = zi1.y;
    lp[2*68] = zi1.z;
    lp[3*68] = zi1.w;
    lp += 68*4;

    lp[0*68] = zi2.x;
    lp[1*68] = zi2.y;
    lp[2*68] = zi2.z;
    lp[3*68] = zi2.w;
    lp += 68*4;

    lp[0*68] = zi3.x;
    lp[1*68] = zi3.y;
    lp[2*68] = zi3.z;
    lp[3*68] = zi3.w;

    barrier(CLK_LOCAL_MEM_FENCE);
}

// Fifth and last pass of 1K FFT
__attribute__((always_inline)) void
kfft_pass5(uint me,
	   const __local float *lds,
	   __global float *gr, __global float *gi)
{
    const __local float *lp;

    // Load registers
    lp = lds + ((me & 0xf) + (me >> 4)*(68*4));

    float4 zr0, zr1, zr2, zr3;

    zr0.x = lp[0*68];
    zr0.y = lp[1*68];
    zr0.z = lp[2*68];
    zr0.w = lp[3*68];
    lp += 16;

    zr1.x = lp[0*68];
    zr1.y = lp[1*68];
    zr1.z = lp[2*68];
    zr1.w = lp[3*68];
    lp += 16;

    zr2.x = lp[0*68];
    zr2.y = lp[1*68];
    zr2.z = lp[2*68];
    zr2.w = lp[3*68];
    lp += 16;

    zr3.x = lp[0*68];
    zr3.y = lp[1*68];
    zr3.z = lp[2*68];
    zr3.w = lp[3*68];

    lp += 68*4*4 - 3*16;

    float4 zi0, zi1, zi2, zi3;

    zi0.x = lp[0*68];
    zi0.y = lp[1*68];
    zi0.z = lp[2*68];
    zi0.w = lp[3*68];
    lp += 16;

    zi1.x = lp[0*68];
    zi1.y = lp[1*68];
    zi1.z = lp[2*68];
    zi1.w = lp[3*68];
    lp += 16;

    zi2.x = lp[0*68];
    zi2.y = lp[1*68];
    zi2.z = lp[2*68];
    zi2.w = lp[3*68];
    lp += 16;

    zi3.x = lp[0*68];
    zi3.y = lp[1*68];
    zi3.z = lp[2*68];
    zi3.w = lp[3*68];

    // Transform
    FFT4();

    // Save result
    __global float4 *gp = (__global float4 *)(gr + (me << 2));
    gp[0*64] = zr0;
    gp[1*64] = zr1;
    gp[2*64] = zr2;
    gp[3*64] = zr3;

    gp = (__global float4 *)(gi + (me << 2));
    gp[0*64] = zi0;
    gp[1*64] = zi1;
    gp[2*64] = zi2;
    gp[3*64] = zi3;
}

// Distance between first real element of successive 1K vectors
// It must be >= 1024, and a multiple of 4
#define VSTRIDE (1024+0)

// Performs a 1K complex FFT with every 64 global ids.
// Each vector is a multiple of VSTRIDE from the first
// Number of global ids must be a multiple of 64, e.g. 1024*64
//
//   greal - pointer to input and output real part of data
//   gimag - pointer to input and output imaginary part of data
__kernel void
forward(__global float *greal, __global float *gimag)
{
    // This is 8704 bytes
    __local float lds[68*4*4*2];

    __global float *gr;
    __global float *gi;
    uint gid = get_global_id(0);
    uint me = gid & 0x3fU;
    uint dg = (gid >> 6) * VSTRIDE;

    gr = greal + dg;
    gi = gimag + dg;

    kfft_pass1(me, gr, gi, lds);
    kfft_pass2(me, lds);
    kfft_pass3(me, lds);
    kfft_pass4(me, lds);
    kfft_pass5(me, lds, gr, gi);
}

