//Sep 9, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class FluxAllAny {
    public static void main(final String[] args) {

	System.out.println("All greater ? " + Flux.just(2, 3, 4)//
		.all(x -> x > 2)//
		.block());

	System.out.println("Any greater ? " + Flux.just(2, 3, 4)//
		.any(x -> x > 2)//
		.block());
    }
}
