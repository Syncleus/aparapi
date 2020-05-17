package com.amd.aparapi.example.chord;

import java.util.Arrays;

public class NoteHistogram{

   NoteValue noteHistogram[];

   float sampleRate;

   float fftWidth;

   int channels;

   NoteHistogram(float _sampleRate, float _fftWidth, int _channels) {
      noteHistogram = new NoteValue[12];
      for (int bin = 0; bin < 12; bin++) {
         noteHistogram[bin] = new NoteValue(bin);
      }
      fftWidth = _fftWidth;
      sampleRate = _sampleRate;
      channels = _channels;

   }

   final double interval = Math.pow(Math.E, Math.log(2) / 12);

   final double middleA = 440.0;

   public int noteVal(double f) { // frequency
      double noteval = Math.log(f / middleA) / Math.log(interval); // log(f/440)/log(12th root of two)  or in our case 440/5 which is still 'A'  
      int noteindex = (int) Math.round(noteval);
      return ((noteindex + (12 * 10)) % 12);
   }

   public void add(float[] re) {
      for (int x = 0; x < fftWidth / 2; x += channels) {

         double freq = (x / channels * sampleRate) / fftWidth;

         int noteIndex = noteVal(freq);
         if (noteIndex >= 0 && noteIndex < 12) {
            for (int channel = 0; channel < channels; channel++) {
               noteHistogram[noteIndex].value += Math.abs(re[x + channel]);
            }
         }
      }
   }

   public void clean() {
      for (int bin = 0; bin < 12; bin++) {
         noteHistogram[bin].value = 0;
         noteHistogram[bin].ratio = 0f;
      }

   }

   public NoteValue[] get() {
      NoteValue[] noteHistogramCopy = Arrays.copyOf(noteHistogram, noteHistogram.length);
      Arrays.sort(noteHistogramCopy);
      float top = noteHistogramCopy[0].value;

      for (int bin = 0; bin < 12; bin++) {
         noteHistogramCopy[bin].ratio = noteHistogramCopy[bin].value / top;

      }
      return noteHistogramCopy;

   }

   @Override public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      NoteValue[] noteHistogramCopy = get();

      for (int bin = 0; bin < 12; bin++) {
         stringBuilder.append(String.format("%s %5.2f ", noteHistogramCopy[bin], noteHistogramCopy[bin].ratio));
      }
      return (stringBuilder.toString());
   }
}
