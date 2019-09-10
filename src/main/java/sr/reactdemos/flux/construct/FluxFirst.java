//Sep 3, 2019
package sr.reactdemos.flux.construct;

import java.time.Duration;

import reactor.core.publisher.Flux;

public class FluxFirst {
    public static void main(final String[] args) {

	// pick the fastest source, the one which emits is't first element first
	Flux.first(//
		Flux.just(1, 2, 3).delayElements(Duration.ofMillis(100)), // Only elements from this will be picked up
		Flux.just(4, 5, 6).delaySequence(Duration.ofMillis(200))// This will be ignored
	)//
		.doOnNext(System.out::println)//
		.blockLast();
    }
}
