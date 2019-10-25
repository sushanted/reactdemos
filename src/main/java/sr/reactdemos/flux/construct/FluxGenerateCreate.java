//Aug 23, 2019
package sr.reactdemos.flux.construct;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscription;

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


	// The final param is the stateConsumer, which can be used to cleanup any used
	// resources (e.g. connection.close())
	Flux.generate(//
		() -> 0, //
		(counter, sink) -> {
		    if (counter < 30) {
			sink.next(++counter);
		    } else {
			sink.complete();
		    }
		    return counter;
		}, //
		counter -> System.out.println("Last counter was: " + counter)//
	)//
		.subscribe(System.out::println);

	final AtomicReference<Subscription> subscriptionHolder = new AtomicReference<>();

	Flux.<Integer>create(emitter -> {

	    // Right one time code to subscribe/register to something
	    // This is called only once
	    // Multiple calls can be made on the emitter
	    final AtomicInteger counter = new AtomicInteger(0);

	    new Thread(() -> {
		while (counter.incrementAndGet() < 40) {
		    emitter.next(counter.get());
		    Utils.sleepForMillis(200);
		}
	    }).start();

	    // cleanup on cancellation
	    emitter.onDispose(() -> {
		System.out.println("Disposing the flux, stopping the counter thread.");
		counter.set(Integer.MAX_VALUE);
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

	// request in timely fashion
	Flux.range(0, 10)//
	// Lower request rate will cause overflow
		.delayElements(Duration.ofMillis(600))//
		.doOnNext(next -> subscription.request(1))//
		.blockLast();

	subscription.cancel();

	/**
	 * create vs push : create and push have exactly same working except the
	 * following difference: <br>
	 * <b>create can work with multi-threaded producer while push can only work with
	 * single-threaded producer</b>
	 **/


    }
}
