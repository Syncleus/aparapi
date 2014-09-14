package com.amd.aparapi.sample.median;

import com.amd.aparapi.*;

/**
 * Provides support for pixel windows of size no greater than 49 (e.g. 7x7).
 * <p/>
 * <p>Demonstrates use of __private array for (partial) sorting, also demonstrates @NoCl annotation for specialised use of ThreadLocal in JTP execution.
 */
public class MedianKernel7x7 extends Kernel {
   public static final int CHANNEL_GRAY = -1;
   public static final int CHANNEL_ALPHA = 0;
   public static final int CHANNEL_RED = 1;
   public static final int CHANNEL_GREEN = 2;
   public static final int CHANNEL_BLUE = 3;

   protected static final int MONOCHROME = 0;
   protected static final int RGB = 1;
   protected static final int ARGB = 2;

   public static final int MAX_WINDOW_SIZE = 49;

   protected int _imageTypeOrdinal;
   protected int[] _sourcePixels;
   protected int _sourceWidth;
   protected int _sourceHeight;

   protected int[] _destPixels;

   // NB could also use suffix naming instead of annotation ... field would be named _window_$private$49
   @PrivateMemorySpace(MAX_WINDOW_SIZE) private short[] _window = new short[MAX_WINDOW_SIZE];
   @NoCL private static ThreadLocal<short[]> _threadLocalWindow = new ThreadLocal<short[]>() {
      @Override
      protected short[] initialValue() {
         return new short[MAX_WINDOW_SIZE];
      }
   };
   protected int _windowWidth;
   protected int _windowHeight;

   @NoCL
   public void setUpWindow() {
      _window = _threadLocalWindow.get();
   }

   public void processImages(MedianSettings settings) {
      _windowWidth = settings.windowWidth;
      _windowHeight = settings.windowHeight;
      execute(_sourceWidth * _sourceHeight);
   }

   @Override
   public void run() {
      setUpWindow();
      int index = getGlobalId();
      int x = index % _sourceWidth;
      int y = index / _sourceWidth;

      int dx0 = -(_windowWidth / 2);
      int dx1 = _windowWidth + dx0;
      int dy0 = -(_windowHeight / 2);
      int dy1 = _windowHeight + dy0;

      int windowX0 = max(0, x + dx0);
      int windowX1 = min(_sourceWidth, x + dx1);
      int windowY0 = max(0, y + dy0);
      int windowY1 = min(_sourceHeight, y + dy1);

      int actualPixelCount = (windowX1 - windowX0) * (windowY1 - windowY0);
      int medianPixel = 0;

      if (_imageTypeOrdinal == MONOCHROME) {
         populateWindow(CHANNEL_GRAY, windowX0, windowX1, windowY0, windowY1);
         medianPixel = median(actualPixelCount);
      } else {
         int alpha = 0xff000000;
         if (_imageTypeOrdinal == ARGB) {
            populateWindow(CHANNEL_ALPHA, windowX0, windowX1, windowY0, windowY1);
            alpha = median(actualPixelCount);
         }
         populateWindow(CHANNEL_RED, windowX0, windowX1, windowY0, windowY1);
         int red = median(actualPixelCount);
         populateWindow(CHANNEL_GREEN, windowX0, windowX1, windowY0, windowY1);
         int green = median(actualPixelCount);
         populateWindow(CHANNEL_BLUE, windowX0, windowX1, windowY0, windowY1);
         int blue = median(actualPixelCount);
         medianPixel = alpha << 24 | red << 16 | green << 8 | blue;
      }

      _destPixels[index] = medianPixel;
   }

   protected void populateWindow(int channel, int windowX0, int windowX1, int windowY0, int windowY1) {
      int windowIndex = 0;
      for (int u = windowX0; u < windowX1; ++u) {
         for (int v = windowY0; v < windowY1; ++v) {
            int argb = _sourcePixels[u + _sourceWidth * v];
            int sourcePixel = valueForChannel(channel, argb);
            setPixelWindowValue(windowIndex, (short) sourcePixel);
            ++windowIndex;
         }
      }
   }

   protected final int valueForChannel(int channel, int argb) {
      int sourcePixel = 0;
      if (channel == CHANNEL_GRAY) {
         sourcePixel = argb;
      } else if (channel == CHANNEL_ALPHA) {
         sourcePixel = (0xff000000 & argb) >>> 24;
      } else if (channel == CHANNEL_RED) {
         sourcePixel = (0x00ff0000 & argb) >>> 16;
      } else if (channel == CHANNEL_GREEN) {
         sourcePixel = (0x0000ff00 & argb) >>> 8;
      } else if (channel == CHANNEL_BLUE) {
         sourcePixel = 0x000000ff & argb;
      }
      return sourcePixel;
   }

   protected void setPixelWindowValue(int windowIndex, short value) {
      _window[windowIndex] = value;
   }

   /**
    * Fast median based on the following algorithm
    * <pre>
    *                   Author: Wirth, Niklaus
    *                    Title: Algorithms + data structures = programs
    *                Publisher: Englewood Cliffs: Prentice-Hall, 1976
    * </pre>
    */
   protected final int median(int actualPixelCount) {
      int i, j, L, m;
      short x;

      L = 0;
      m = actualPixelCount - 1;
      while (L < m) {
         x = _window[(actualPixelCount / 2)];
         i = L;
         j = m;
         do {
            while (_window[i] < x) {
               i++;
            }
            while (x < _window[j]) {
               j--;
            }
            if (i <= j) {
               short temp = _window[i];
               _window[i] = _window[j];
               _window[j] = temp;
               i++;
               j--;
            }
         } while (i <= j);

         if (j < actualPixelCount / 2) {
            L = i;
         }
         if (actualPixelCount / 2 < i) {
            m = j;
         }
      }
      return _window[(actualPixelCount / 2)];
   }
}
