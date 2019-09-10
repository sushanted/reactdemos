//Sep 9, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxCache {
    public static void main(final String[] args) {

	demoSimpleCache();

	demoExpiry();

	demoHistory();

	demoExpiryAndHistory();

    }

    private static void demoSimpleCache() {
	final long startTime = System.currentTimeMillis();

	final Flux<Long> hotTimePublisher = Flux.range(0, 5)//
		.delayElements(Duration.ofMillis(100))//
		.map(i -> System.currentTimeMillis() - startTime);

	final Flux<Long> cachedPublisher = hotTimePublisher.cache();

	System.out.println("Hot times:");
	hotTimePublisher.doOnNext(System.out::println).blockLast();

	System.out.println("cached times:");
	cachedPublisher.doOnNext(System.out::println).blockLast();

	Utils.sleepForSeconds(2);

	System.out.println("cached times after sleep: (same every time)");
	cachedPublisher.doOnNext(System.out::println).blockLast();

	System.out.println("Hot times again:");
	hotTimePublisher.doOnNext(System.out::println).blockLast();
    }

    private static void demoExpiry() {

	final long startTime = System.currentTimeMillis();

	final Flux<Long> hotTimePublisher = Flux.range(0, 10)//
		.doOnSubscribe(Utils.print("Hot subscriber got subscribed!"))
		.delayElements(Duration.ofMillis(1000))// Delay to vary expiry for each element
		.map(i -> System.currentTimeMillis() - startTime);

	// Expire items emitted before 4 seconds from now, expiry is applied only at the
	// time of
	// caching (i.e. till the completion of the hot publisher)
	// Once caching is complete, same items will retain in the cache (cached
	// publisher) till ttl, and will be emitted by the cached publisher on any
	// further subscriptions to it till ttl.
	// After that caching is done again, subscribing to hot publisher.
	final Flux<Long> cachedPublisher = hotTimePublisher.cache(Duration.ofSeconds(4));

	for (int i = 0; i < 15; i++) {
	    System.out.println("Subscription number : " + i);
	    cachedPublisher//
		    .doOnSubscribe(Utils.print("Cached publisher got subscribed"))//
		    .doOnNext(System.out::println)//
		    .blockLast();
	    Utils.sleepForMillis(1000);
	}

    }

    private static void demoHistory() {

	final Flux<Integer> cachedPublisher = Flux.range(0, 10)//
		.cache(5);

	System.out.println("This time caching will happen, so all elements will be printed");
	cachedPublisher.doOnNext(System.out::println).blockLast();

	System.out.println("This time only 5 latest cached values will be printed");
	cachedPublisher.doOnNext(System.out::println).blockLast();

    }

    private static void demoExpiryAndHistory() {
	final long startTime = System.currentTimeMillis();

	final Flux<Long> hotPublisher = Flux.range(0, 10)//
		.delayElements(Duration.ofMillis(500))// Delay to vary expiry for each element
		.map(i -> System.currentTimeMillis() - startTime);

	final Flux<Long> timeStringentCachedPublisher = hotPublisher//
		.cache(8, Duration.ofSeconds(3));

	timeStringentCachedPublisher.blockLast();

	System.out.println("Only max last 8 items will be cached, items till last 3 seconds will be cached");
	timeStringentCachedPublisher.doOnNext(System.out::println).blockLast();

	final Flux<Long> historyStringentCachedPublisher = hotPublisher//
		.cache(3, Duration.ofSeconds(3));

	historyStringentCachedPublisher.blockLast();

	System.out.println("Only max last 3 items will be cached, items till last 3 seconds will be cached");
	historyStringentCachedPublisher.doOnNext(System.out::println).blockLast();

    }
}
