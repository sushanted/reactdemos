//Oct 1, 2019
package sr.reactdemos.flux.operators;

import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxLimits {
    public static void main(final String[] args) {

	// limit number of requests : this operator never let the upstream produce more
	// elements than the cap
	System.out.println("Limit number of request");
	Flux.range(0, 10)//
		.limitRequest(4)//
		.log()
		.blockLast();

	System.out.println("\nLimit rate default replenish (75%)");
	final AtomicInteger emittedCount = new AtomicInteger();
	// 100 is upper-bound, in no scenario more than 100 requests would be made
	Flux.range(0, 1000)//
		.doOnRequest(reqs -> System.out.printf(//
			"Items emitted till: %3d, requested next: %3d\n", //
			emittedCount.get(), //
			reqs//
		)//
		)//
		.limitRate(100)//
		.doOnNext(n -> emittedCount.getAndIncrement())//
		.blockLast();

	System.out.println("\nLimit rate custom replenish");
	emittedCount.set(0);
	// 100 is upper-bound, in no scenario more than 100 requests would be made
	// At every replenish point, the total current requests are matched to the
	// highTide(prefetch value)
	// If prefetch is 100 and lowTide is 70, once next 70 items are emitted, new
	// request for 70 items is made
	// (30 items are yet to be sent by producer, but are already requested, the next
	// request is 70 : making
	// total request to 30+70=100)
	// Effectively at any point in time, the publisher has at max 100 in-flight
	// requests, and attempt is made to keep it at 100.
	// same process is repeated till completion
	Flux.range(0, 1000)//
		.doOnRequest(//
			reqs -> System.out.printf(//
				"Items emitted till: %3d, requested next: %3d\n", //
				emittedCount.get(), //
				reqs//
			)//
		)//
		 // if lowTide >= prefetch : defaulted to 75
		 // if lowTide == 0 : next requests are made only after all prefetch items are
		 // emitted
		.limitRate(100, 99)//
		.doOnNext(n -> emittedCount.getAndIncrement())//
		.blockLast();

	System.out.println("\nSame as the default limit rate");
	Flux.range(0, 1000)//
		.doOnRequest(reqs -> System.out.printf(//
			"Items emitted till: %3d, requested next: %3d\n", //
			emittedCount.get(), //
			reqs//
		)//
		)//
		.publishOn(Schedulers.immediate(), 100)//
		.doOnNext(n -> emittedCount.getAndIncrement())//
		.blockLast();

    }
}
