/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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
package com.aparapi.runtime;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.internal.tool.ABCCKernel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class MultithreadTest {
    static float[] t = new float[]{416667f, 416668f, 416669f, 416670f, 416671f, 416672f, 416673f, 416674f, 416675f, 416676f, 416677f, 416678f, 416679f, 416680f, 416681f, 416682f, 416683f, 416684f, 416685f, 416686f, 416687f, 416688f, 416689f, 416690f, 416691f, 416692f, 416693f, 416694f, 416695f, 416696f, 416697f, 416698f, 416699f, 416700f, 416701f, 416702f, 416703f, 416704f, 416705f, 416706f, 416707f, 416708f, 416709f, 416710f, 416711f, 416712f, 416713f, 416714f, 416715f, 416716f, 416717f, 416718f, 416719f, 416720f, 416721f, 416722f, 416723f, 416724f, 416725f, 416726f, 416727f, 416728f, 416729f, 416730f, 416731f, 416732f, 416733f, 416734f, 416735f, 416736f, 416737f, 416738f, 416739f, 416740f, 416741f, 416742f, 416743f, 416744f, 416745f, 416746f, 416747f, 416748f, 416749f, 416750f, 416751f, 416752f, 416753f, 416754f, 416755f, 416756f, 416757f, 416758f, 416759f, 416760f, 416761f, 416762f, 416763f, 416764f, 416765f, 416766f, 416767f, 416768f, 416769f, 416770f, 416771f, 416772f, 416773f, 416774f, 416775f, 416776f, 416777f, 416778f, 416779f, 416780f, 416781f, 416782f, 416783f, 416784f, 416785f, 416786f, 416787f, 416788f, 416789f, 416790f, 416791f, 416792f, 416793f, 416794f, 416795f, 416796f, 416797f, 416798f, 416799f, 416800f, 416801f, 416802f, 416803f, 416804f, 416805f, 416806f, 416807f, 416808f, 416809f, 416810f, 416811f, 416812f, 416813f, 416814f, 416815f, 416816f, 416817f, 416818f, 416819f, 416820f, 416821f, 416822f, 416823f, 416824f, 416825f, 416826f, 416827f, 416828f, 416829f, 416830f, 416831f, 416832f, 416833f, 416834f, 416835f, 416836f, 416837f, 416838f, 416839f, 416840f, 416841f, 416842f, 416843f, 416844f, 416845f, 416846f, 416847f, 416848f, 416849f, 416850f, 416851f, 416852f, 416853f, 416854f, 416855f, 416856f, 416857f, 416858f, 416859f, 416860f, 416861f, 416862f, 416863f, 416864f, 416865f, 416866f};
    static float[] p = new float[]{2034.91f, 2042.03f, 2044.13f, 2063.15f, 2060.7f, 2056.29f, 2057.51f, 2058.18f, 2047.97f, 2001.26f, 2008.72f, 1999.9f, 1981.21f, 1933.88f, 1923.55f, 1939.44f, 1929.48f, 1893.06f, 1910.54f, 1930.46f, 1940.75f, 1929.77f, 1914.37f, 1872.92f, 1831.74f, 1857.52f, 1832.32f, 1830.06f, 1785.75f, 1784.81f, 1796.98f, 1809.07f, 1791.85f, 1768.04f, 1754.01f, 1741.92f, 1765.34f, 1785.87f, 1807.05f, 1797.9f, 1813.57f, 1758.5f, 1775.85f, 1768.07f, 1725.15f, 1716.43f, 1731.33f, 1761.02f, 1760.95f, 1742.41f, 1734.22f, 1731.33f, 1711.96f, 1710.56f, 1663.85f, 1656.57f, 1616.43f, 1611.78f, 1669.43f, 1651.7f, 1662.17f, 1707.62f, 1698.16f, 1669.59f, 1623.32f, 1622.89f, 1648.25f, 1685.88f, 1670.64f, 1694f, 1722.13f, 1706.31f, 1705.01f, 1735.95f, 1743.88f, 1775.36f, 1818.09f, 1812.17f, 1813.04f, 1775.78f, 1756.78f, 1756.94f, 1764.34f, 1812.26f, 1794.48f, 1802.72f, 1865.28f, 1863.09f, 1843.68f, 1893.35f, 1882.88f, 1915.17f, 1934.48f, 1912f, 1907.62f, 1910.38f, 1881.93f, 1914.18f, 1933.92f, 1952.46f, 1974.38f, 1954.74f, 1958.4f, 1956.65f, 1993.21f, 2000.73f, 2019.83f, 2045.57f, 2047.59f, 2002.27f, 2030.48f, 2037.54f, 2043.64f, 2069.43f, 2043.62f, 1992.19f, 2011.31f, 1964.96f, 1992.72f, 1985.37f, 2038.22f, 2018.29f, 2041.33f, 2053.25f, 2086.2f, 2079.23f, 2061.43f, 2060.43f, 2047.58f, 2027.91f, 2004.27f, 2042.32f, 2022.79f, 1999.28f, 1995.37f, 1989.62f, 1945.19f, 1961.19f, 1968.31f, 1971.8f, 1968.05f, 1980.25f, 2009.48f, 2026.87f, 2022.79f, 2023.39f, 2041.25f, 2021.78f, 2022.01f, 2026.15f, 2030.88f, 2041.01f, 2141.41f, 2190.04f, 2248.67f, 2221.63f, 2270.04f, 2259.56f, 2249.08f, 2280.56f, 2284.33f, 2348.77f, 2364.71f, 2438.89f, 2436.04f, 2370.25f, 2411.94f, 2341.18f, 2330.4f, 2377.23f, 2400.77f, 2372f, 2373.93f, 2324.76f, 2355f, 2328.78f, 2369.15f, 2392.48f, 2376.29f, 2359.96f, 2340.38f, 2339.7f, 2304.06f, 2286.95f, 2258.27f, 2268.42f, 2266.97f, 2317.76f, 2295.68f, 2299.64f, 2323.04f, 2340.8f, 2332.45f, 2316.51f, 2351.41f, 2362.82f, 2378.29f, 2404.29f, 2410.59f, 2443.15f};

    @Test
    public void test1() throws InterruptedException {
        test(1, 1);
        test(1, 2);
    }
    @Test
    public void test4() throws InterruptedException {
        test(4, 1);
        test(4, 2);
    }
    @Test
    public void test7() throws InterruptedException {
        test(7, 1);
    }
    @Test
    public void test9() throws InterruptedException {
        test(9, 1);
    }

//    @Test
//    public void testN() throws InterruptedException {
//        int granularity = 3;
//        for (int t = 2; t< Math.max(granularity,Runtime.getRuntime().availableProcessors())+ granularity; t+= granularity) {
////            test(t, t/2);
//            test(t, t);
////            test(t, t*2);
////            test(t, t*2+1); //odd
//        }
//    }

    static void test(int THREADS, int KERNELS) throws InterruptedException {

        final int E = (KERNELS * 8); //10000;

        final List<ABCCKernel> kernels = new ArrayList(KERNELS);
        for (int i = 0; i < KERNELS; i++) {
            kernels.add( new ABCCKernel(t, p) );
        }

        final ExecutorService f = Executors.newFixedThreadPool(
            THREADS
        );

        // repeat until we eventually crash
        final AtomicInteger next = new AtomicInteger(0);

        for (int i = 0; i< E; i++) {
            f.execute(() ->
                kernels.get(next.getAndIncrement() % KERNELS).execute(Range.create(t.length)));
        }

        f.shutdown();
        f.awaitTermination(1, TimeUnit.MINUTES);

        for (Kernel k : kernels)
            k.dispose();
    }

//    static class ABCCKernel extends Kernel {
//        public static final int FI = 0, GI = 1, HI = 2, YI = 3, FI2 = 4, FIGI = 5, GI2 = 6, YIFI = 7, YIGI = 8, FIHI = 9, GIHI = 10, HI2 = 11, YIHI = 12;
//        public static final int v = 13;
//        private static final int TC = 0, M = 1, W = 2;
//        private float[] T, p;
//        private float[] tcmw = new float[3];
//        private float[] result;
//        //public final int N;
//
//        public ABCCKernel(float[] t, float[] p) {
//            setExplicit(true);
//            this.T = t;
//            this.p = p;
//            this.result = new float[t.length * v];
//            put(this.T).put(this.p).put(result);
//        }
//
//        public void setNewTandP(float[] t, float[] p) {
//            this.T = t;
//            this.p = p;
//            this.result = new float[t.length * v];
//            put(this.T).put(this.p).put(result);
//        }
//
//        public void set_tcmw(float tc, float m, float w) {
//            this.tcmw = new float[]{tc, m, w};
//            put(this.tcmw);
//        }
//
//        @Override
//        public void run() {
//            int i = getGlobalId();
//            int j = i * v;
//            int fi = FI + j, gi = GI + j, hi = HI + j, yi = YI + j, fi2 = FI2 + j, figi = FIGI + j, gi2 = GI2 + j, yifi = YIFI + j, yigi = YIGI + j, fihi = FIHI + j, gihi = GIHI + j, hi2 = HI2 + j, yihi = YIHI + j;
//            float tc = tcmw[TC];
//            float w = tcmw[W];
//            float m = tcmw[M];
//
//            float[] r = this.result;
//
//            r[fi] = pow((tc - T[i]), m);
//            r[gi] = r[fi] * cos(w * log(tc - T[i])); //TODO check if this inner log is computed once and shared
//            r[hi] = r[fi] * sin(w * log(tc - T[i]));
//            r[yi] = p[i];
//            r[fi2] = r[fi] * r[fi];
//            r[figi] = r[fi] * r[gi];
//            r[gi2] = r[gi] * r[gi];
//            r[yifi] = r[yi] * r[fi];
//            r[yigi] = r[yi] * r[gi];
//            r[fihi] = r[fi] * r[hi];
//            r[gihi] = r[gi] * r[hi];
//            r[hi2] = r[hi] * r[hi];
//            r[yihi] = r[yi] * r[hi];
//        }
//
//        public float[] getResult() {
//            get(result);
//            return result;
//        }
//    }

}
