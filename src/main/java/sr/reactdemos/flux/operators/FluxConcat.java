//Sep 10, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxConcat {
    public static void main(final String[] args) {

	// Similar to flatMap but subscribes to inners (A,B,C) one after completion of
	// another

	System.out.println("\nConcat map with publisher mapper");
	Flux.just("A", "B", "C")//
		.concatMap(//
			chr -> Flux.range(1, 3)//
				.map(String::valueOf)//
				.map(chr::concat)//
				.doOnSubscribe(Utils.print("Subscribed to " + chr))//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nConcat map with iterable mapper");
	Flux.just("A", "B", "C")//
		.concatMapIterable(//
			chr -> Flux.range(1, 3)//
				.map(String::valueOf)//
				.map(chr::concat)//
				.collectList()//
				.block())//
		.doOnNext(System.out::println)//
		.blockLast();

	// Type of other publisher should be same as the flux
	System.out.println("\nConcat with another publisher");
	Flux.just("A", "B", "C")//
		.concatWith(//
			Flux.just("E", "F", "G")//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

	// Type of other values should be same as the flux
	System.out.println("\nConcat with other values");
	Flux.just("A", "B", "C")//
		.concatWithValues("E", "F", "G")//
		.doOnNext(System.out::println)//
		.blockLast();
    }
}
