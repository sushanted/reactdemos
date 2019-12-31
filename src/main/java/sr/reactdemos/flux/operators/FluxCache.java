//Sep 9, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;
import sr.reactdemos.utils.Utils.TimeTracker;

/***
 * This is very similar to FluxReplay, with the only difference : replay returns
 * connectable Flux and it starts emitting only after connecting to it.
 * 
 * TODO : merge this with FluxReplay if possible
 *
 */
public class FluxCache {
    public static void main(final String[] args) {

	demoSimpleCache();

	demoExpiry();

	demoHistory();

	// Longer expiry : history will clear the oldest
	// Note: exactly 4 items are immediately (@0) obtained by late subscribers
	demoHistoryAndExpiry(4, Duration.ofMillis(500));

	// Short expiry : expiry will clear the expired items
	// Note: less than 4 items are immediately (@0) obtained by late subscribers
	// First item @ 0 and next item @<50
	demoHistoryAndExpiry(4, Duration.ofMillis(50));
    }

    private static void demoSimpleCache() {

	Utils.demo("Simple cache");

	final TimeTracker timeTracker = new TimeTracker();

	final Flux<Long> hotTimePublisher = Flux.range(0, 5)//
		.delayElements(Duration.ofMillis(100))//
		.map(i -> timeTracker.elapsed());

	final Flux<Long> cachedPublisher = hotTimePublisher.cache();

	System.out.println("Hot times:");
	hotTimePublisher.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("cached times:");
	cachedPublisher.doOnNext(System.out::println)//
		.blockLast();

	Utils.sleepForSeconds(2);

	System.out.println("cached times after sleep: (same every time)");
	cachedPublisher.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("Hot times again:");
	hotTimePublisher.doOnNext(System.out::println)//
		.blockLast();
    }

    private static void demoExpiry() {

	Utils.demo("Expiry");

	final Flux<Integer> cached = Flux.range(0, 10)//
		.doOnSubscribe(Utils.print("Subscribed to original"))//
		.delayElements(Duration.ofMillis(100))//
		// TTL for each item is 50ms
		.cache(Duration.ofMillis(300));

	final TimeTracker timeTracker = new TimeTracker();

	// This will get all the items, as it has subscribed before ANY cached items
	// expired
	cached//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Early subscriber"))//
		.subscribe();

	Utils.sleepForMillis(300);

	// First few items immediately from cache
	cached//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 1"))//
		.subscribe();

	Utils.sleepForMillis(500);

	// First few items immediately from cache, will miss the expired items
	cached//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 2"))//
		.subscribe();

	Utils.sleepForMillis(700);

	// All the items are expired till this time, it will re-subscribe to original
	cached//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Super late subscriber"))//
		.subscribe();

	Utils.sleepForSeconds(4);

    }

    /**
     * Only last n items will be cached any time. On arrival of new item, oldest
     * cached item will be discarded. A new subscriber will get the cached items
     * immediately and any next emitted items as they get emitted, it will loose the
     * discarded items(discarded before it subscribed) from the cache.
     */
    private static void demoHistory() {

	Utils.demo("History");

	final Flux<Integer> cachedPublisher = Flux.range(0, 10)//
		.delayElements(Duration.ofMillis(50))
		.cache(5);

	final TimeTracker timeTracker = new TimeTracker();

	cachedPublisher//
		.doOnSubscribe(s -> timeTracker.lapse())//
		.doOnNext(timeTracker.printValue("Early subscriber"))//
		.subscribe();

	Utils.sleepForMillis(300);

	cachedPublisher//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 1"))//
		.subscribe();

	Utils.sleepForMillis(300);

	cachedPublisher//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 2"))//
		.subscribe();

	Utils.sleepForSeconds(2);

    }

    private static void demoHistoryAndExpiry(final int history, final Duration expiry) {

	Utils.demo("History and expiry");

	final Flux<Integer> cachedFlux = Flux.range(0, 10)//
		.delayElements(Duration.ofMillis(50))//
		.cache(history, expiry);

	final TimeTracker timeTracker = new TimeTracker();

	// This will get all the items as it has subscribed started before connect
	cachedFlux
		//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Early subscriber"))//
		.subscribe();

	Utils.sleepForMillis(400);

	// This will miss few items, as it subscribed late
	// This will get first <history> items immediately from cache, then other items
	// with
	// delay
	cachedFlux//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 1"))//
		.subscribe();

	Utils.sleepForMillis(100);
	// This will miss few more items
	cachedFlux//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 2"))//
		.subscribe();

	// Wait enough for all to complete
	Utils.sleepForSeconds(2);
    }

}
