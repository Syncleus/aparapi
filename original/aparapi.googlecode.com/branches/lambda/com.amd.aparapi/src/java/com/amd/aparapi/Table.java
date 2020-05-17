package com.amd.aparapi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 3/30/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
class Table{

   final static String spaces = "                                                                                                                        ";

   private List<Col> cols = new ArrayList<Col>();

   private int size = 0;

   private int col = 0;

   static class Col{
      private List<String> text = new ArrayList<String>();

      private int width;

      private String format = "%s";

      Col(String _format){
         format = _format;
      }

      Col(){
         this("%s");
      }

      void format(Object... args){
         String s = String.format(format, args);

         width = Math.max(s.length(), width);
         text.add(s);
      }

      int size(){
         return (text.size());
      }

      String pad(String _s, int _width){
         int length = _s.length();
         int padWidth = _width - length;
         String padded = _s + spaces.substring(0, padWidth);
         return (padded);

      }

      String get(int _i){

         return (pad(text.get(_i), width));
      }

      void header(String _header){
         text.add(_header);
         width = _header.length();
      }
   }

   Table(String... _formats){
      for(String format : _formats){
         cols.add(new Col(format));
      }
   }

   void data(Object... args){
      cols.get(col++).format(args);
      if(col == cols.size()){
         col = 0;
         size++;
      }
   }

   @Override
   public String toString(){
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < size; i++){
         for(Table.Col col : cols){
            sb.append(col.get(i));
         }
         sb.append("\n");
      }
      return (sb.toString());
   }

   void header(String... _headers){
      for(int i = 0; i < _headers.length; i++){
         cols.get(i).header(_headers[i]);
      }
      size++;

   }

}

