//Sep 30, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

public class FluxGroup {
    public static void main(final String[] args) {

	System.out.println("\n Simple grouping based on remainder of 3 ");
	Flux.range(0, 20)//
		.groupBy(k -> k % 3)//
		.doOnNext(gf -> System.out.println("KEY: " + gf.key()))// 3 GroupFlux will be emitted
		.concatMap(f -> f)// sequentially subscribe to each group flux and map them
		.doOnNext(System.out::println)//
		.blockLast();


	System.out.println("\n Grouping with key and value mapper");
	Flux.range(0, 20)//
		.groupBy(k -> k % 3, v -> Tuples.of(v % 3, v))//
		.doOnNext(gf -> System.out.println("KEY: " + gf.key()))// 3 GroupFlux will be emitted
		.concatMap(f -> f)// sequentially subscribe to each group flux and map them
		.doOnNext(System.out::println)//
		.blockLast();


    }
}
