/**
 * 
 */
package com.amd.aparapi.example.chord;

class NoteValue implements Comparable<NoteValue>{

   float value;

   int bin;

   boolean sharp = false;

   public float ratio;

   final static String sharpNotes[] = new String[] {
         "A",
         "A#",
         "B",
         "C",
         "C#",
         "D",
         "D#",
         "E",
         "F",
         "F#",
         "G",
         "G#"
   };

   final static String flatNotes[] = new String[] {
         "A",
         "Bb",
         "B",
         "C",
         "Db",
         "D",
         "Eb",
         "E",
         "F",
         "Gb",
         "G",
         "Ab"
   };

   public NoteValue(int _bin) {
      bin = _bin;
   }

   @Override public int compareTo(NoteValue o) {
      return (Float.compare(o.value, value));
   }

   @Override public String toString() {
      return (sharp ? sharpNotes[bin] : flatNotes[bin]);
   }

}
