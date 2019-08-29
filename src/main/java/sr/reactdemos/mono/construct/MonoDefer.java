package sr.reactdemos.mono.construct;

import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Mono;

public class MonoDefer {
    public static void main(final String[] args) {

	final AtomicInteger subscriptionCount = new AtomicInteger();

	// Internal Mono can be changed at runtime, depending on the system/other
	// conditions
	// Mono can be changed on the fly
	final Mono<String> deferredMono = Mono.defer(() -> {
	    // This code will be called on each subscription
	    System.out.println("Subscribed!!");
	    // This mono finally will be subscribed
	    return Mono.just("hello " + subscriptionCount.incrementAndGet());
	});

	deferredMono.subscribe(System.out::println);
	deferredMono.subscribe(System.out::println);
    }
}
