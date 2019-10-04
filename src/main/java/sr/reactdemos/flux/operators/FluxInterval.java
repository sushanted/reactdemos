//Sep 30, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;

public class FluxInterval {
    public static void main(final String[] args) {

	System.out.println("Interval with period");
	Flux.interval(Duration.ofMillis(100))//
		.limitRequest(10)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nInterval with delay and period");
	Flux.interval(Duration.ofMillis(1000), Duration.ofMillis(100))//
		.limitRequest(10)//
		.doOnNext(System.out::println)//
		.blockLast();

    }
}
