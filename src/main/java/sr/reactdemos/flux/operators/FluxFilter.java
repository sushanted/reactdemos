//Sep 20, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class FluxFilter {
    public static void main(final String[] args) {

	System.out.println("\nFilter with onError and onDiscard support (Error item will also be filterred out)");
	Flux.range(-5, 10)//
		.filter(i -> (120 / i) % 20 == 0)//
		.doOnDiscard(Object.class, discarded -> System.out.println("Discarded: " + discarded))//
		.onErrorContinue((t, o) -> System.out.println(o + " erred with " + t))//
		.subscribe(System.out::println);

	System.out.println("\nFilter when odd");
	Flux.range(0, 10)//
		.filterWhen(i -> Mono.just(i % 2 == 0))//
		.subscribe(System.out::println);

	System.out.println("\nFilter when with max buffer");
	Flux.range(0, 10)//
		.doOnRequest(i -> System.out.println("Requested " + i))//

		.filterWhen(//
			i -> Mono.just(true)//
				.delayElement(Duration.ofMillis(2))//
				.doOnNext(Utils.print("Filter completed for " + i)), //
			// max number of values kept pending for the publisher to publish
			2//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nType based filter");
	Flux.just(1.01d, 2.0, 3, "x", 4, "y")//
		.ofType(Number.class)//
		.doOnDiscard(Object.class, Utils.printValue("Discarding non number"))//
		.ofType(Integer.class)//
		.doOnDiscard(Object.class, Utils.printValue("Discarding non integer"))//
		.blockLast();

    }
}
