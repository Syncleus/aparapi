#PossibleAparapiLambdaSyntaxOptions
*syntax suggestions for HSA enabled Aparapi*

#Introduction
Now that Java 8 is nearly upon us and HSA enabled Aparapi 'lambda' branch is usable (though in no way complete) I figured we could use this page to discuss the 'programming model' we might prefer for Aparapi, and contrast with the API's for the new Java 8 lambda based stream APIs.

##Converting between Aparapi HSA + Java 8 enabled Aparapi
Our **hello world** app has always been the ''vector add''. In classic Aparapi we could transform

    final float inA[] = .... // get a float array from somewhere
    final float inB[] = .... // get a float from somewhere
                         // assume (inA.length==inB.length)
    final float result = new float[inA.length];

    for (int i=0; i<array.length; i++){
        result[i]=intA[i]+inB[i];
    }
to

    Kernel kernel = new Kernel(){
       @Override public void run(){
          int i= getGlobalId();
          result[i]=intA[i]+inB[i];
       }
    };
    Range range = Range.create(result.length);
    kernel.execute(range);
For the lambda aparapi branch we can currently use

    Device.hsa().forEach(result.length, i-> result[i]=intA[i]+inB[i]);
Note that the closest Java 8 construct is

    IntStream.range(0, result.length).parallel().forEach(i-> result[i]=intA[i]+inB[i]);
Aparapi and Java 8 stream API's both use IntConsumer as the lambda type. So you can reuse the lambda.

    IntConsumer lambda = i-> result[i]=intA[i]+inB[i];

    IntStream.range(0, result.length).parallel().forEach(lambda);
    Device.hsa().forEach(result.length, lambda);
Exposing the Deviceness of this was a conscious effort. We may also hide it completely.

    IntConsumer lambda = i-> result[i]=intA[i]+inB[i];

    IntStream.range(0, result.length).parallel().forEach(lambda);
    Aparapi.forEach(result.length, lambda);
I am toying with providing an API which maps more closely to the Stream API from Java 8.

Maybe

    IntStream.range(0, result.length).parallel().forEach(lambda);
    Aparapi.range(0, result.length).parallel().forEach(lambda);
This way users can more readily swap between the two.

For collections/arrays in Aparapi we can also offer

    T[] arr = // get an array of T from somewhere
    ArrayList<T> list = // get an array backed list of T from somewhere

    Aparapi.range(arr).forEach(t -> /* do something with each T */);
We can create special cases. Say for mutating images

    BufferedImage in, out;
    Aparapi.forEachPixel(in, out, rgb[] -> rgb[0] = 0 );
We may also need select operations for associative operations

    class Person{
        int age;
        String first;
        String last;
    };

    Aparapi.selectOne(Person[] people, (p1,p2)-> p1.age>p2.age?p1:p2 );
##A case for map reduce
A mapper maps from one type to another. Possibly by extracting state. Here is a mapper which maps each String in an array of Strings to its length.

As if the mapper was

    interface mapToInt<T>{ int map(T v); }
Here it is in action.

    Aparapi.range(strings).map(s->string.length())...
Now the result is a stream of int's which can be 'reduced' by a reduction lambda.

In this case the reduction reduces two int's to one, by choosing the max of k and v. All reductions must be commutative style operations (max, min, add) where the order of execution is not important.

    int lengthOfLongestString = Aparapi.range(strings).map(s->string.length()).reduce((k,v)-> k>v?k:v);
Here we had a sum reduction.

    int sumOfLengths = Aparapi.range(strings).map(s ->string.length()).reduce((k,v)-> k+v);
Some of these may be common enough that we offer direct functionality.

    int sumOfLengths = Aparapi.range(strings).map(s ->string.length()).sum();
    int maxOfLengths = Aparapi.range(strings).map(s ->string.length()).max();
    int minOfLengths = Aparapi.range(strings).map(s ->string.length()).min();
    String string = Aparapi.range(strings).map(s->string.length()).select((k,v)-> k>v);
This last one needs some explaining. We map String to int then select the String whose length is the greatest.