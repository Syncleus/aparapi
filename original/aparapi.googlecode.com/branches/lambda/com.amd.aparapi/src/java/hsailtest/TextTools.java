package hsailtest;

import com.amd.aparapi.AparapiException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.IntConsumer;

import static com.amd.aparapi.Device.*;


public class TextTools {

    interface LineProcessor{
        String line(String line);
    }
    static void  process(File _inFile, File _outFile, LineProcessor _lineProcessor) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(_outFile)));
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_inFile)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            bw.append(_lineProcessor.line(line)).append("\n");

        }
        br.close();
        bw.close();
    }

    static void  process(File _inFile,  LineProcessor _lineProcessor) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_inFile)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            _lineProcessor.line(line);

        }
        br.close();
    }
    static String getText(InputStream _is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(_is));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            sb.append(" ").append(line);
        }
        br.close();
        _is.close();
        return(sb.toString());
    }

    static String getLowercaseText(InputStream _is) throws IOException {

        return(getText( _is).toLowerCase());
    }
    static String getLowercaseText(File _file) throws IOException {
       return(getLowercaseText(new FileInputStream(_file)));

    }

    enum State { WS, TEXT, SINGLE, DOUBLE};

    static String[] getSentences(File _file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            sb.append(" ").append(line);
        }
        br.close();
        String asString = sb.toString();
        List<String> sentences = new ArrayList<String>();
        Stack<State> stateStack = new Stack<State>();
        stateStack.push(State.WS);
        int firstNonWs = 0;
        for (int index = 0; index<asString.length(); index++){
           char ch = asString.charAt(index);
           switch (stateStack.peek()){
               case WS:
                   if (Character.isWhitespace(ch)){

                   } else if (ch == '\''){
                       stateStack.pop();
                       stateStack.push(State.SINGLE);
                   }
           }
        }
        return(sentences.toArray(new String[0]));
    }

    static char[] getLowercaseTextChars(File _file) throws IOException {
        return(getLowercaseText(_file).toCharArray());
    }

    static char[] getLowercaseTextChars(InputStream _is) throws IOException {
        return(getLowercaseText(_is).toCharArray());
    }

    static char[] getLowercaseTextCharsOnly(File _file) throws IOException {
        char[] chars =  getLowercaseText(_file).toCharArray();
        for (int i=0; i<chars.length; i++){
            if (!Character.isAlphabetic(chars[i])){
                chars[i]=' ';
            }
        }
        return(chars);
    }

    static char[] getLowercaseTextCharsOnly(InputStream _is) throws IOException {
        char[] chars =  getLowercaseText(_is).toCharArray();
        for (int i=0; i<chars.length; i++){
            if (!Character.isAlphabetic(chars[i])){
                chars[i]=' ';
            }
        }
        return(chars);
    }
   static String getString (InputStream inputStream) throws IOException {
      String text = getLowercaseText(inputStream);
      inputStream.close();
      return(text);

   }
    static String getString (URL url) throws IOException {
        return (getString(url.openConnection().getInputStream()));


    }

   static String getLowerCaseString (File _file) throws IOException {
      InputStream is = new FileInputStream(_file);

      return(getLowercaseText(is));

   }

    static String getString (File _file) throws IOException {
        InputStream is = new FileInputStream(_file);

        return(getText(is));

    }
    static String[] buildDictionary(File _file) throws IOException {
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            if (!line.trim().startsWith("//")){
                list.add(line.trim()) ;
            }else{
                System.out.println("Comment -> "+line);
            }
        }
        while((list.size()%256)!=0){
            list.add("xxxxx");
        }

        return(list.toArray(new String[0]));
    }

    static String[] buildLowerCaseDictionary(File _file) throws IOException {
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            if (!line.trim().startsWith("//")){
                list.add(line.trim().toLowerCase()) ;
            }else{
                System.out.println("Comment -> "+line);
            }
        }
        while((list.size()%64)!=0){
            list.add("xxxxx");
        }

        return(list.toArray(new String[0]));
    }

    static char[][] buildLowerCaseDictionaryChars(File _file) throws IOException {
        String[] lowerCaseDictionary=buildLowerCaseDictionary(_file);
        char[][] chars = new char[lowerCaseDictionary.length][];
        for (int i=0; i<lowerCaseDictionary.length; i++){
            chars[i]=lowerCaseDictionary[i].toCharArray();
        }

        return(chars);
    }

    static char[][] buildWhiteSpacePaddedDictionaryChars(File _file) throws IOException {
        String[] dictionary=buildDictionary(_file);
        char[][] chars = new char[dictionary.length][];
        for (int i=0; i<dictionary.length; i++){
            chars[i]=(" "+dictionary[i]+" ").toCharArray();
        }

        return(chars);
    }

   public  static void main(String[] args) throws IOException {

        String s = getString(new URL("http://www.gutenberg.org/cache/epub/1023/pg1023.txt"));
        System.out.println(s);
        //process(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"), new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt.out"),
              //  line->{
              //     if (line.trim().equals("") || line.trim().startsWith("//")){
                 //      return(line);
                //  }else{
                  //     return(line.substring(0,1).toUpperCase()+line.substring(1).toLowerCase());
                 ///  }
               // });
    }


}
