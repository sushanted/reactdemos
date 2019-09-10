//Sep 10, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class FluxCasting {

    public static void main(final String[] args) {

	Flux.just(1, "2", 3, "4")// it is flux of serializable
		.filter(Integer.class::isInstance)//
		.cast(Integer.class)// it is now flux of integer
		.subscribe(System.out::println);

	Flux.just(1, "2", 3, "4")// it is flux of serializable
		.ofType(Integer.class)// does filter and cast both
		.doOnDiscard(//
			Object.class, // it is now flux of integer
			discarded -> System.out.println("Unsupported type: " + discarded.getClass())//
		)//
		.subscribe(System.out::println);

    }

}
