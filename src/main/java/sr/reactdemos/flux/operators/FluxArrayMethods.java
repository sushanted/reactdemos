//Sep 10, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class FluxArrayMethods {
    public static void main(final String[] args) {
	System.out.println("Number of elements: " + Flux.just(1, 2, 3, 4, 5).count().block());

	System.out.println("Elememnt at index: " + Flux.just(1, 2, 3, 4, 5).elementAt(2).block());

	System.out.println("Elememnt at index (default if AIOB): " + Flux.just(1, 2, 3, 4, 5).elementAt(5, -1).block());

	System.out.println("First element: " + Flux.just(1, 2, 3).next().block());

	System.out.println("Last element: " + Flux.just(1, 2, 3, 4, 5).last().block());

	System.out.println("Last element (default): " + Flux.just().last(-1).block());

	System.out.println("Has element : " + Flux.just(1, 2, 3, 4, 5).hasElement(3).block());

	System.out.println("Has element : " + Flux.just(1, 2, 3, 4, 5).hasElement(0).block());

	System.out.println("Has ANY elements : " + Flux.just(1, 2, 3, 4, 5).hasElements().block());

	System.out.println("Has ANY elements : " + Flux.just().hasElements().block());

	System.out.println("Index of each element: ");


    }
}
