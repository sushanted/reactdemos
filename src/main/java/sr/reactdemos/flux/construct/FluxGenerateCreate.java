//Aug 23, 2019
package sr.reactdemos.flux.construct;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscription;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import sr.reactdemos.utils.Utils;

public class FluxGenerateCreate {

    public static void main(final String[] args) {

	final AtomicInteger state = new AtomicInteger();

	// Need to maintain an external state in this variant
	Flux.generate(//
		sink -> {
		    // only one call can be made in one sink callback
		    if (state.get() < 30) {
			sink.next(state.incrementAndGet());
		    } else {
			sink.complete();
		    }
		})//
		.subscribe(System.out::println);

	// The state is every-time provided by the callback
	Flux.generate(() -> 0, (counter, sink) -> {
	    if (counter < 30) {
		sink.next(++counter);
	    } else {
		sink.complete();
	    }
	    return counter;
	})//
		.subscribe(System.out::println);

	final AtomicReference<Subscription> subscriptionHolder = new AtomicReference<>();

	// The final param is the stateConsumer, which can be used to cleanup any used
	// resources (e.g. connection.close())
	Flux.generate(() -> 0, (counter, sink) -> {
	    if (counter < 30) {
		sink.next(++counter);
	    } else {
		sink.complete();
	    }
	    return counter;
	}, counter -> System.out.println("Last counter was: " + counter))//
		.subscribe(System.out::println);

	// Create a separate class about backpressure and its handling
	final Disposable disposable = Flux.<Integer>create(emitter -> {

	    // Right one time code to subscribe/register to something
	    // This is called only once
	    // Multiple calls can be made on the emitter
	    final AtomicBoolean done = new AtomicBoolean(false);

	    new Thread(() -> {
		while (!done.get()) {
		    try {
			emitter.next(System.in.read());
		    } catch (final IOException e) {
			done.set(true);
		    }
		}
	    }).start();

	    System.out.println("You got 20 seconds to feed the flux:");

	    // cleanup on cancellation

	    emitter.onDispose(() -> {
		System.out.println("Disposing the flux, stopping the console input thread");
		done.set(true);
	    });
	}, OverflowStrategy.BUFFER)//
		.subscribe(//
			System.out::println, //
			System.err::println, //
			() -> {
			}, //
			subscriptionHolder::set//
		);

	final Subscription subscription = subscriptionHolder.get();

	System.out.println("Ready to accept 5 requests");
	subscription.request(5);

	Utils.sleepForSeconds(10);

	System.out.println("Again ready to accept 5 requests");
	subscription.request(5);

	// Find a better way to cancel the flux
	Utils.sleepForSeconds(5);

	subscription.cancel();

    }
}
