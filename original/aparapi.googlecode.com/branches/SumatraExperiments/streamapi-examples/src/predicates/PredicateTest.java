package predicates; 
//import  static java.util.stream.primitive.PrimitiveStreams.range;
import  static java.util.stream.Streams.intRange;
import  java.util.function.Predicates;
import  java.util.function.Predicate;
import  java.util.function.IntPredicate;
import  java.util.function.IntBlock;

public class PredicateTest{
   public static void main(String[] _args) {
     IntPredicate multipleOfThree = val -> val%3==0;
     IntPredicate multipleOfTwo = val -> val%2==0;
     
     intRange(0, 12).forEach(table -> {
          //range(0, 12).filter(multipleOfThree.and(v2->v2%2==0))
          //range(0, 12).filter(multipleOfTwo.and(multipleOfThree))
          intRange(0, 12).filter(multipleOfTwo.and(v -> v%3==0))
//          range(0, 12).filter(Predicates<Integer>.and(v1 -> v1%2==0, v2->v2%2==0))
          .forEach(value -> {
             System.out.printf("%d * %d = %d\n", value, table, (value*table));
          }
       );
       System.out.printf("\n");
       }
     );
   }
}
