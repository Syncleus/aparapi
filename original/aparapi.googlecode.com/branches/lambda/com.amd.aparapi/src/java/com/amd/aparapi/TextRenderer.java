package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class TextRenderer<T extends TextRenderer>{
   final protected StringBuilder sb = new StringBuilder();
   private int lastNewLineIndex = 0;
   private int lastMark = 0;



   final public T append(String s){
      sb.append(s);
      return ((T) this);
   }

   final public T append(Number v){
      if(v instanceof Integer){
         append(((Integer) v).intValue());
      }else if(v instanceof Float){
         append(((Float) v).floatValue()).append("f");
      }else if(v instanceof Long){
         append(((Long) v).longValue()); // .append("l");   ?

      }else if(v instanceof Double){
         append(((Double) v).doubleValue());
      }else{
         append("what?");
      }
      return ((T) this);
   }


   final public T append(int i){
      return (append("" + i));

   }

   final public T append(double d){
      return (append("" + d));
   }

   final public T append(float f){
      return (append("" + f));
   }

   final public T append(long l){
      return (append("" + l));
   }

   final public T appendConst(int i){
      return (append(i));

   }

   final public T appendConst(double d){
      return (append(d));
   }

   final public T appendConst(float f){
      append(f);
      return (append("f"));
   }

   final public T appendConst(long l){
      append(l);
      return (append("L"));
   }


   final public T commaSpace(){
      return (append(", "));
   }

   int lineNumber = 1;
   boolean showLineNumbers = false;

   public T setShowLineNumbers(boolean _showLineNumbers){
      showLineNumbers = _showLineNumbers;
      return ((T) this);
   }

   boolean showComments = false;

   public T setShowComments(boolean _showComments){
      showComments = _showComments;
      return ((T) this);
   }

   public boolean isShowingComments(){
      return (showComments);
   }

   final T commentStart(){
       return (append("/*"));
   }
    final T commentEnd(){
        return (append("*/"));
    }
    final public T comment(String text){
        commentStart().space().append(text).commentEnd();
        return ((T)this);
    }

   final public T nl(){
      append("\n");
      lastMark = lastNewLineIndex = sb.length();
      if(showLineNumbers){
         commentStart().append(lineNumber).commentEnd();
         lineNumber++;
      }
      return ((T) this);
   }

    final public T nl(int n){
       for (int i=0; i<n; i++){
           nl();
       }
       return ((T) this);
    }


   final public T pad(int n){
      while(sb.length() - lastNewLineIndex < n){
         space();
      }
      return ((T) this);
   }


   final public T mark(){
      lastMark = sb.length();
      return ((T) this);
   }

   final public T relpad(int n){
      while(sb.length() - lastMark < n){
         space();
      }
      return ((T) this);
   }

   final public T space(){
      return (append(" "));
   }

   final public T semicolon(){
        return (append(";"));
    }

    final public T lineCommentStart(){
        return (append("// "));
    }

    final public T lineComment(String text){
        lineCommentStart().space().append(text).nl();
        return ((T)this);
    }
    final public T colon(){
        return (append(":"));
    }
    final public T obrace(){
        return (append("{"));
    }
    final public T cbrace(){
        return (append("}"));
    }

    final public T oparenth(){
        return (append("("));
    }
    final public T cparenth(){
        return (append(")"));
    }

   final public T dot(){
      return (append("."));
   }

   final public T append(TextRenderer _t){
      return (append(_t.toString()));
   }

   @Override
   final public String toString(){
      return (sb.toString());
   }

}