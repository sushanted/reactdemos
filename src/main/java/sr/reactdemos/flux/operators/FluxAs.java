//Sep 9, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class FluxAs {
    // TODO later rename as convert and include other conversion methods
    // (compose,transform)
    public static void main(final String[] args) {
	// Flux to a concrete single object
	System.out.println(Flux.just(1, 2, 4).as(flux -> flux.collectList().block()));
    }
}
