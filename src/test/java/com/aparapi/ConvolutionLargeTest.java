/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import com.aparapi.Kernel;
import com.aparapi.internal.model.CacheEnabler;
import com.aparapi.internal.model.Supplier;
import org.junit.Test;

public class ConvolutionLargeTest {

    private static final float CONVOLUTION_MATRIX[] = new float[]{
        0f,
        -10f,
        0f,
        -10f,
        40f,
        -10f,
        0f,
        -10f,
        0f,
    };
    private static final int TEST_ROUNDS = 1;
    private static final int SECONDS_PER_TEST = 2;
    private static final int SECONDS_PER_WARMUP = 1;

    @Test
    public void testConvolutionLarge() {
        boolean testWithoutCaches = true;
        for (int i = 1; i <= TEST_ROUNDS; i++) {
            int pixels = (1_000 * 1_000 * (1 << (i - 1))) & ~(1 << 10 - 1);

            //prepare the size
            int side = (int) Math.sqrt(pixels);
            int width = side;
            int height = side;
            byte[] inBytes = new byte[width * height * 3];
            byte[] outBytes = new byte[width * height * 3];

            testWithSupplier(new ImageConvolutionCreationContext() {
                private ImageConvolution convolution = new ImageConvolution();

                @Override
                public Supplier<ImageConvolution> getSupplier() {
                    return new Supplier<ImageConvolution>() {
                        @Override
                        public ImageConvolution get() {
                            return convolution;
                        }
                    };
                }

                @Override
                public Consumer<ImageConvolution> getDisposer() {
                    return new Consumer<ImageConvolution>() {
                        @Override
                        public void accept(ImageConvolution k) {
                            // Do nothing
                        }
                    };
                }

                @Override
                public void shutdown() {
                    convolution.dispose();
                }

                @Override
                public String getName() {
                    return "single kernel";
                }
            }, SECONDS_PER_TEST, testWithoutCaches, inBytes, outBytes, width, height);
            testWithSupplier(new ImageConvolutionCreationContext() {
                @Override
                public Supplier<ImageConvolution> getSupplier() {
                    return new Supplier<ImageConvolution>() {
                        @Override
                        public ImageConvolution get() {
                            return new ImageConvolution();
                        }
                    };
                }

                @Override
                public Consumer<ImageConvolution> getDisposer() {
                    return new Consumer<ImageConvolution>() {
                        @Override
                        public void accept(ImageConvolution k) {
                            k.dispose();
                        }
                    };
                }

                @Override
                public void shutdown() {
                    // Do nothing
                }

                @Override
                public String getName() {
                    return "multiple kernels";
                }
            }, SECONDS_PER_TEST, testWithoutCaches, inBytes, outBytes, width, height);
        }
    }

    private void testWithSupplier(ImageConvolutionCreationContext imageConvolutionCreationContext, int seconds, boolean testWithoutCaches, byte[] inBytes, byte[] outBytes, int width, int height) {
        CacheEnabler.setCachesEnabled(!testWithoutCaches);
        // Warmup
        doTest( SECONDS_PER_WARMUP, imageConvolutionCreationContext, inBytes, outBytes, width, height);
        if (testWithoutCaches) {
            long timeWithoutCaches = doTest( seconds, imageConvolutionCreationContext, inBytes, outBytes, width, height);
            CacheEnabler.setCachesEnabled(true);
            long timeWithCaches = doTest( seconds, imageConvolutionCreationContext, inBytes, outBytes, width, height);
        } else {
            doTest( seconds, imageConvolutionCreationContext, inBytes, outBytes, width, height);
        }
    }

    //   @FunctionalInterface
    private interface Consumer<K> {
        void accept(K k);
    }

    private interface ImageConvolutionCreationContext {
        Supplier<ImageConvolution> getSupplier();

        Consumer<ImageConvolution> getDisposer();

        void shutdown();

        String getName();

    }

    private long doTest(int seconds, ImageConvolutionCreationContext imageConvolutionCreationContext, byte[] inBytes, byte[] outBytes, int width, int height) {
        long totalTime = 0;
        Supplier<ImageConvolution> imageConvolutionSupplier = imageConvolutionCreationContext.getSupplier();
        Consumer<ImageConvolution> disposer = imageConvolutionCreationContext.getDisposer();
        int calls = 0;
        long initialTime = System.nanoTime();
        long maxElapsedNs = TimeUnit.SECONDS.toNanos(seconds);
        for (; ; ) {
            long start = System.nanoTime();
            if (start - initialTime > maxElapsedNs)
                break;
            ImageConvolution imageConvolution = imageConvolutionSupplier.get();
            try {
                imageConvolution.applyConvolution(CONVOLUTION_MATRIX, inBytes, outBytes, width, height);
            } finally {
                disposer.accept(imageConvolution);
            }

            long end = System.nanoTime();
            long roundTime = end - start;
            totalTime += roundTime;
            calls++;
        }
        imageConvolutionCreationContext.shutdown();
        return totalTime / calls;
    }

    final static class ImageConvolution extends Kernel {

        private float convMatrix3x3[];

        private int width, height;

        private byte imageIn[], imageOut[];

        public void processPixel(int x, int y, int w, int h) {
            float accum = 0f;
            int count = 0;
            for (int dx = -3; dx < 6; dx += 3) {
                for (int dy = -1; dy < 2; dy += 1) {
                    final int rgb = 0xff & imageIn[((y + dy) * w) + (x + dx)];

                    accum += rgb * convMatrix3x3[count++];
                }
            }
            final byte value = (byte) (max(0, min((int) accum, 255)));
            imageOut[(y * w) + x] = value;

        }

        @Override
        public void run() {
            final int x = getGlobalId(0) % (width * 3);
            final int y = getGlobalId(0) / (width * 3);

            if ((x > 3) && (x < ((width * 3) - 3)) && (y > 1) && (y < (height - 1))) {
                processPixel(x, y, width * 3, height);
            }

        }

        public void applyConvolution(float[] _convMatrix3x3, byte[] inBytes, byte[] outBytes, int width, int height) {
            imageIn = inBytes;
            imageOut = outBytes;
            this.width = width;
            this.height = height;
            convMatrix3x3 = _convMatrix3x3;
            execute(3 * this.width * this.height);
        }
    }
}
