package hsailtest;

import com.amd.aparapi.Device;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;


public class GetDickens3 {

   public class Name {
      char[] name;
      int count1;
       int count2;
      Name(char[] _chars){
         name = _chars;
         reset();
      }
      int getCount(){
         return (count1+count2);
      }
      void reset(){
          count1 = 0;
          count2 = 0;
      }
   }
   public class Book {
      String title;
      char[] text;
      URL url;
      File file;

      Book(String _title, URL _url) {
         title = _title;
         url = _url;
      }
      Book(String _title, File _file) {
         title = _title;
         file = _file;
      }

      void get() {
         System.out.print("Reading " + title);
         try {
            if (url != null){
               text = TextTools.getString(url).toCharArray();
            }else if (file != null) {
               text = TextTools.getString(file).toCharArray();
            }
            for (int i=0; i<text.length; i++){
                if (!Character.isAlphabetic(text[i])){
                    text[i]=' ';
                }
            }
         } catch (IOException e) {
            text = new char[0];
            System.out.print("failed! ");
         }
         System.out.println();
      }

      void checkNames(Device device, Name[] names, int pass) {

         int textLen = text.length;
         device.forEach(names.length, gid -> {
            Name name = names[gid];
            char[] nameChars = name.name;
            int nameCharLen = nameChars.length;
            int count=0;
            if (pass==1){
                count = name.count2;
            }  else{
                count = name.count1;
            }

            for (int i = 0; i < textLen - nameCharLen; i++) {
                     int offset = 0;
                     while (offset<nameCharLen && (nameChars[offset] == text[i + offset])){
                        offset++;
                     }
                      if (offset==nameCharLen){
                          count++;
                      }
                  }
             if (pass==1){
                 name.count2=count;
             }                       else{
                 name.count1=count;
             }
         });

      }
   }


   public static void main(String[] args) throws IOException {
      new GetDickens3().go();
   }

   public void test(String type, Device dev, Book[] library, Name[] names){

      long start = System.currentTimeMillis();

      //for (Book book : library) {
         library[0].checkNames(dev, names, 0);
       library[1].checkNames(dev, names, 1);
      //}

      long end = System.currentTimeMillis();

     // Name[] sortedNames = Arrays.copyOf(names, names.length);


    //  Arrays.sort(sortedNames, new Comparator<Name>(){
      //   @Override public int compare(Name lhs, Name rhs){

       //    return (rhs.getCount()-lhs.getCount());

       //  }
     // });
      System.out.print(type+" -> "+(end-start));
      boolean first = true;
      for (int i = 0; i < names.length; i++) {
         if (names[i].getCount()>0) {
            if (!first) {
               System.out.print(", ");
            } else {
               first = false;
            }

            for (char c : names[i].name) {
               System.out.print(c);
            }
            System.out.print("("+names[i].getCount()+", "+ names[i].count1+", "+ names[i].count2+")");
         }

      }
      System.out.println();

   }

   public void go() throws IOException {
      //File lambdaDir = new File("/Users/garyfrost/aparapi/aparapi/branches/lambda");
      File lambdaDir = new File("C:\\Users\\user1\\aparapi\\branches\\lambda");
      File booksDir = new File(lambdaDir, "books/dickens");
      Book[] library1 = new Book[] {

        // new Book("A Tail Of Two Cities", new File(booksDir, "ATailOfTwoCities.txt")),
             new Book("A Christmas Carol", new File(booksDir, "AChristmasCarol.txt")),
            new Book("Great Expectations", new File(booksDir, "GreatExpectations.txt")),

       //     new Book("David Copperfield", new File(booksDir, "DavidCopperfield.txt")),
            // new Book("Nicolas Nickleby", new File(booksDir,"NicolasNickleby.txt")),
             // new Book("Collection", new File(booksDir,"CollectedWorks.txt")),
             //new Book("Oliver Twist", new File(booksDir,"OliverTwist.txt")),

      };
      // Book[] library2 = new Book[] {

               //  new Book("A Tail Of Two Cities", new File(booksDir, "ATailOfTwoCities.txt")),
               //  new Book("A Christmas Carol", new File(booksDir, "AChristmasCarol.txt")),
              // new Book("Great Expectations", new File(booksDir, "GreatExpectations.txt")),

               // new Book("David Copperfield", new File(booksDir, "DavidCopperfield.txt")),
              //  new Book("Nicolas Nickleby", new File(booksDir,"NicolasNickleby.txt")),
              // new Book("Collection", new File(booksDir,"CollectedWorks.txt")),
               //new Book("Oliver Twist", new File(booksDir,"OliverTwist.txt")),

    //   };
      Device.seq().forEach(library1.length, i -> library1[i].get());
      // Device.seq().forEach(library2.length, i -> library2[i].get());

      char[][] nameChars = TextTools.buildWhiteSpacePaddedDictionaryChars(new File(lambdaDir, "names.txt"));
      Name[] names1 = new Name[nameChars.length];

       for (int i=0; i<names1.length; i++){  names1[i] = new Name(nameChars[i]);}

       for (int i=0; i<names1.length; i++){  names1[i].reset();}
      test("hsa", Device.hsa(), library1, names1);
       for (int i=0; i<names1.length; i++){  names1[i].reset();}
      test("hsa", Device.hsa(), library1, names1);
       for (int i=0; i<names1.length; i++){  names1[i].reset();}
      test("jtp", Device.jtp(), library1, names1);
       for (int i=0; i<names1.length; i++){  names1[i].reset();}
       test("seq", Device.seq(), library1, names1);
       for (int i=0; i<names1.length; i++){ names1[i].reset();}
     //  Name[] names2 = new Name[nameChars.length];

      // for (int i=0; i<names2.length; i++){  names2[i] = new Name(nameChars[i]);}

      // test("hsa", Device.hsa(), library2, names2);
     //  for (int i=0; i<names2.length; i++){  names2[i].count = 0;}

     //  test("hsa", Device.hsa(), library2, names2);
     //  for (int i=0; i<names2.length; i++){  names2[i].count = 0;}
      // test("jtp", Device.jtp(), library2, names2);
      // for (int i=0; i<names2.length; i++){  names2[i].count = 0;}
    //   Device.seq().forEach(names.length, i -> {names[i].accumCount = 0; for (int b=0; b<library.length;b++) names[i].count[b]=0;});
    //  test("seq", Device.seq(), library, names);


   }


}
