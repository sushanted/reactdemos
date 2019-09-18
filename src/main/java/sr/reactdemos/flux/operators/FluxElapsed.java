//Sep 11, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class FluxElapsed {
    public static void main(final String[] args) {
	Flux.range(0, 10)//
		.delayElements(Duration.ofMillis(200)).elapsed()//
		.doOnNext(System.out::println)//
		.blockLast();

	final AtomicInteger ai = new AtomicInteger(0);

	Mono.fromRunnable(() -> {
	    if (ai.incrementAndGet() < 40) {
		throw new Error("sorry");
	    }
	})//
		.doOnSubscribe(Utils.print("sub"))
		.onErrorResume(t -> Mono.error(t).delaySubscription(Duration.ofMillis(500)))//
		.retry(10)//
		.block();
    }
}
