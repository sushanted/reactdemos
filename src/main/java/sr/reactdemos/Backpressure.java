//Aug 29, 2019
package sr.reactdemos;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sr.reactdemos.utils.Utils;

public class Backpressure {
    public static void main(final String[] args) {

	System.out.println("Will not miss any output from the producer because of buffering");
	checkBackpressureStrategy(OverflowStrategy.BUFFER);

	System.out.println("\nNot allow a slow consumer, scream with an Error!!");
	checkBackpressureStrategy(OverflowStrategy.ERROR);

	System.out.println("\nDrop the elements if request is not made");
	checkBackpressureStrategy(OverflowStrategy.DROP);

	System.out.println("\nKeep only latest elements, drop the older ones ");
	checkBackpressureStrategy(OverflowStrategy.LATEST);

	System.out.println(
		"\nIgnore requests : Producer don't care about requests and bombard the subscriber with whatever is produced");
	checkBackpressureStrategy(OverflowStrategy.IGNORE);

    }

    private static void checkBackpressureStrategy(final OverflowStrategy overflowStrategy) {
	final AtomicReference<Subscription> subscriptionHolder = new AtomicReference<>();

	Flux.<Integer>create(emitter -> {

	    // Right one time code to subscribe/register to something
	    // This is called only once
	    // Multiple calls can be made on the emitter
	    final AtomicInteger counter = new AtomicInteger(0);

	    new Thread(() -> {
		while (counter.incrementAndGet() < 40) {
		    System.out.println("Producing: " + counter.get());
		    emitter.next(counter.get());
		    Utils.sleepForMillis(200);
		}
	    }).start();

	    // cleanup on cancellation
	    emitter.onDispose(() -> {
		System.out.println("Disposing the flux, stopping the counter thread.");
		counter.set(40);
	    });
	}, overflowStrategy)//
		.doOnRequest(reqs -> System.out.println("Request made"))//
		.doOnDiscard(Integer.class, i -> System.out.println("Discarding: " + i))//
		.subscribe(//
			next -> System.out.println("Consuming: " + next), //
			System.err::println, //
			() -> {
			}, //
			subscriptionHolder::set//
		);

	final Subscription subscription = subscriptionHolder.get();

	// request in timely fashion
	Flux.range(0, 10)//
		// Lower request rate will cause overflow
		.delayElements(Duration.ofMillis(400))//
		.doOnNext(next -> subscription.request(1))//
		.blockLast();

	subscription.cancel();
    }
}
