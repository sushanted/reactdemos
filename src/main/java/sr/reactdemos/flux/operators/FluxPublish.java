//Oct 25, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class FluxPublish {
    public static void main(final String[] args) {

	demoPublish();

	Utils.demo("Publish (this is very different than the earlier)");
	Flux//
		.range(0, 10)//
		.doOnSubscribe(Utils.print("subscribed to original, this will happen only once!")).publish(f -> {
		    // multi-cast the flux to multiple subscribers
		    // can do multiple independent operations on the values
		    f.map(x -> x + x).subscribe(Utils.printValue("Doubler"));
		    f.map(x -> x * x).subscribe(Utils.printValue("Squerer"));
		    return f;
		})//
		.blockLast();

	demoPublishNext();
    }

    private static void demoPublishNext() {
	Utils.demo("Publish next");
	final Mono<Long> hotPublisher = Flux.range(1, 5)//
		.doOnSubscribe(Utils.print("subscribed to original, this will happen only once!"))//
		.map(i -> System.currentTimeMillis())//
		.publishNext(); // try using next() instead of publishNext() : it will cause independent
				// subscription for each subscriber

	System.out.println("First subscriber:" + hotPublisher.block());
	// At this point the value is cached into the publisher and will be used forever
	System.out.println("Second subscriber:" + hotPublisher.block());
	Utils.sleepForMillis(500);
	System.out.println("Second subscriber:" + hotPublisher.block());
    }

    private static void demoPublish() {
	final ConnectableFlux<Long> connectableFlux = Flux//
		.interval(Duration.ofMillis(200))//
		// This is printed only once, as subscription is done only once for all the
		// subscribers
		.doOnSubscribe(Utils.print("Connectable flux got subscribed"))//
		// this will emit only after EVERY subscriber requests at-least one item
		.publish();

	connectableFlux
		.doOnNext(Utils.printValue("Simple subscriber"))//
		.limitRequest(10)//
		.subscribe();

	connectableFlux.doOnNext(Utils.printValue("Slow subscriber"))//
		// This is a slow subscriber, it will cause the multicast to pause for each
		// request/emit : it will cause delay for all the subscribers
		.doOnNext(n -> Utils.nonReactiveSleepForMillis(500))//
		.limitRate(1, 0)//
		.limitRequest(10)//
		.subscribe();

	connectableFlux.connect();

	Utils.sleepForMillis(3000);

	connectableFlux//
		.timestamp()//
		.doOnNext(Utils.printValue("Late subscriber"))//
		.limitRequest(10)//
		.blockLast();
    }
}
