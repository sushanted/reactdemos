//Sep 30, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxPrefetch {

    public static void main(final String[] args) {

	// prefetch is related to the size of the request

	System.out.println("Default prefetch: " + Flux.range(0, 10).getPrefetch());

	System.out.println("Custom prefetch: " + Flux.range(0, 10).limitRate(5).getPrefetch());

	System.out.println("FlatMap prefetch: " + Flux.range(0, 10).flatMap(Mono::just).getPrefetch());

	System.out.println("FlatMap custom prefetch: " + Flux.range(0, 10).flatMap(Mono::just, 3, 4).getPrefetch());
    }
}
