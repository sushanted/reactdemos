//Nov 15, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;
import sr.reactdemos.utils.Utils.TimeTracker;

/**
 * This is very similar to FLux.cache, just that it returns a ConnectableFlux,
 * which
 *
 */
public class FluxReplay {
    public static void main(final String[] args) {

	demoSimple();

	demoHistory();

	demoExpiry();

	// Longer expiry : history will clear the oldest
	// Note: exactly 4 items are immediately (@0) obtained by late subscribers
	demoHistoryAndExpiry(4, Duration.ofMillis(500));

	// Short expiry : expiry will clear the expired items
	// Note: less than 4 items are immediately (@0) obtained by late subscribers
	// First item @ 0 and next item @<50
	demoHistoryAndExpiry(4, Duration.ofMillis(50));

    }

    /**
     * Expire each item in the cache after the specified duration. A late subscriber
     * would get the cached items immediately but other items later as they get
     * available. If a subscriber subscribes late and if the flux has finished, the
     * original flux will be re-subscribed.
     */
    private static void demoExpiry() {

	Utils.demo("Expiry");

	final ConnectableFlux<Integer> connectable = Flux.range(0, 10)//
		.doOnSubscribe(Utils.print("Subscribed to original"))//
		.delayElements(Duration.ofMillis(100))//
		// TTL for each item is 50ms
		.replay(Duration.ofMillis(300));

	final TimeTracker timeTracker = new TimeTracker();

	// This will get all the items, as it has subscribed before ANY cached items
	// expired
	connectable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Early subscriber"))//
		.subscribe();

	// This shouldn't affect, as yet we are not connected
	Utils.sleepForMillis(100);

	// Emittion will start only after connect
	System.out.println("Connecting...");
	connectable.connect();

	Utils.sleepForMillis(300);

	// First few items immediately from cache
	connectable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 1"))//
		.subscribe();

	Utils.sleepForMillis(500);

	// First few items immediately from cache, will miss the expired items
	connectable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 2"))//
		.subscribe();

	Utils.sleepForMillis(700);

	// All the items are expired till this time, it will re-subscribe to original
	connectable//
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

	final ConnectableFlux<Integer> connectable = Flux.range(0, 10)//
		.delayElements(Duration.ofMillis(50))//
		.replay(4);

	// This will get all the items as it has subscribed before connect
	final TimeTracker timeTracker = new TimeTracker();
	connectable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Early subscriber"))//
		.subscribe();

	Utils.sleepForMillis(100);
	// The values would have been cached at this time

	// Emittion will start only after connect
	System.out.println("Connecting...");
	connectable.connect();

	Utils.sleepForMillis(400);

	// This will miss few items, as it subscribed late
	// This will get first 4 items immediately from cache, then other items with
	// delay
	connectable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 1"))//
		.subscribe();

	Utils.sleepForMillis(100);
	// This will miss few more items
	connectable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 2"))//
		.subscribe();

	// Wait enough for all to complete
	Utils.sleepForSeconds(2);
    }


    private static void demoHistoryAndExpiry(final int history, final Duration expiry) {

	Utils.demo("History and expiry");

	final ConnectableFlux<Integer> connectable = Flux.range(0, 10)//
		.delayElements(Duration.ofMillis(50))//
		.replay(history, expiry);

	final TimeTracker timeTracker = new TimeTracker();

	// This will get all the items as it has subscribed started before connect
	connectable
		//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Early subscriber"))//
		.subscribe();

	Utils.sleepForMillis(100);
	// The values would have been cached at this time

	// Emittion will start only after connect
	System.out.println("Connecting...");
	connectable.connect();

	Utils.sleepForMillis(400);

	// This will miss few items, as it subscribed late
	// This will get first <history> items immediately from cache, then other items
	// with
	// delay
	connectable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 1"))//
		.subscribe();

	Utils.sleepForMillis(100);
	// This will miss few more items
	connectable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber 2"))//
		.subscribe();

	// Wait enough for all to complete
	Utils.sleepForSeconds(2);
    }


    private static void demoSimple() {

	Utils.demo("Simple");

	final TimeTracker timeTracker = new TimeTracker();

	final ConnectableFlux<?> replayable = Flux.range(0, 10)//
		.timestamp()//
		.delayElements(Duration.ofMillis(100))//
		.map(t -> t.getT2() + " " + (System.currentTimeMillis() - t.getT1()))//
		.doOnNext(Utils.printValue("actual"))//
		.replay();

	replayable.doOnNext(timeTracker.printValue("Early subscriber")).subscribe();

	System.out.println("First subscriber subscribed, but waiting for connect");
	Utils.sleepForMillis(2000);

	replayable.connect();
	System.out.println("Connected");

	Utils.sleepForMillis(500);

	// This will get first 4-5 items fast, but later will face the 100ms delay
	replayable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Late subscriber"))//
		.subscribe();

	Utils.sleepForMillis(1100);

	// This will get all the cached items and very fast, because they are readily
	// cached!
	replayable//
		.doOnSubscribe(timeTracker::lapse)//
		.doOnNext(timeTracker.printValue("Very late subscriber"))
		.subscribe();

	Utils.sleepForSeconds(3);
    }
}
