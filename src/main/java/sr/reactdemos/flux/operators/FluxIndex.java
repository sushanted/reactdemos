//Sep 30, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class FluxIndex {
    public static void main(final String[] args) {
	Flux.just("A", "B", "C")//
		.index()//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\n index with mapper");
	Flux.range(0, 10)//
		.index((index, element) -> index * element)//
		.doOnNext(System.out::println)//
		.blockLast();
    }
}
