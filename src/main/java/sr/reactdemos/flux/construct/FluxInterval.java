//Sep 3, 2019
package sr.reactdemos.flux.construct;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxInterval {
    public static void main(final String[] args) {

	System.out.println("\nInterval");
	Flux.interval(Duration.ofMillis(200))//
		.doOnNext(System.out::println)//
		.doOnNext(next -> {
		    if (next == 10)
			throw new RuntimeException();
		})//
		.onErrorReturn(-1l)//
		.blockLast();

	System.out.println("\nInterval with delay");
	Flux.interval(Duration.ofMillis(1000), Duration.ofMillis(300))//
		.doOnNext(System.out::println)//
		.doOnNext(next -> {
		    if (next == 10)
			throw new RuntimeException();
		})//
		.onErrorReturn(-1l)//
		.blockLast();

	final AtomicReference<Subscription> subscriptionHolder = new AtomicReference<>();

	Flux.interval(Duration.ofMillis(200))//
		.doOnNext(System.out::println)//
		.subscribe(//
			o -> {
			}, //
			t -> {
			    System.out.println("No timely requests received: " + t.getMessage());
			}, //
			() -> {
			}, //
			subscriptionHolder::set//
		);

	subscriptionHolder.get().request(7);

	Utils.sleepForSeconds(3);

    }
}
