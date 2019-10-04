//Sep 10, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class SimpleFluxOps {
    public static void main(final String[] args) {
	System.out.println(//
		"Default value: " + //
		Flux.range(0, 5)//
			.filter(x -> x > 5)//
			.defaultIfEmpty(-1)//
			.blockLast()//
	);

	System.out.println(Flux.range(0, 5).ignoreElements().block());
    }
}
