//Sep 10, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class FluxArrayMethods {
    public static void main(final String[] args) {
	System.out.println("Number of elements: " + Flux.just(1, 2, 3, 4, 5).count().block());

	System.out.println("Elememnt at index: " + Flux.just(1, 2, 3, 4, 5).elementAt(2).block());
    }
}
