//Sep 9, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class FluxDelay {
    public static void main(final String[] args) {

	// The delay strategy looks to be keep distance of time n between two successive
	// emissions
	// when they are available, else wait for next emissions

	System.out.println("Delay elements:");

	final long startTime = System.currentTimeMillis();

	Flux.create(emitter -> {
	    Utils.sleepForMillis(1000);
	    emitter.next(1);
	    Utils.sleepForMillis(2000);
	    emitter.next(2);
	    Utils.sleepForMillis(5000);
	    emitter.next(3);
	    Utils.sleepForMillis(2000);
	    emitter.next(4);
	    Utils.sleepForMillis(1000);
	    emitter.next(5);
	    emitter.complete();
	})//
		.elapsed()//
		.doOnNext(
			next -> System.out.println("Emitted: " + next + " " + (System.currentTimeMillis() - startTime)))//
		.delayElements(Duration.ofMillis(2000))// Every element is delayed after emission
		.elapsed()//
		.doOnNext(
			next -> System.out.println("Delayed: " + next + " " + (System.currentTimeMillis() - startTime)))//
		.blockLast();

	System.out.println("Delaying sequence");
	Flux.range(1, 10)//
		.doOnSubscribe(Utils.print("Subscribed now"))//
		.delaySequence(Duration.ofSeconds(1))//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("Delaying subscription");
	Flux.range(1, 10)//
		.doOnSubscribe(Utils.print("Subscribed now"))//
		.delaySubscription(Duration.ofSeconds(2))//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("Delaying subscription on repeat (more interesting case!)");
	Flux.range(1, 4)//
		.doOnSubscribe(Utils.print("Subscribed"))//
		.delaySubscription(Duration.ofSeconds(2))//
		.repeat(3)// Wait for 2 seconds before every subscription
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("Delaying subscription using companion");
	Flux.range(1, 3)//
		.doOnSubscribe(Utils.print("Subscribed now"))//
		.delaySubscription(Mono.delay(Duration.ofSeconds(2)))//
		.repeat(3)
		.doOnNext(System.out::println)//
		.blockLast();

	// Can have dynamic delay dependent on the input
	System.out.println("Delay until");
	Flux.range(1, 10)//
		.delayUntil(x -> Mono.delay(Duration.ofMillis(x * 100)))//
		.doOnNext(System.out::println)//
		.blockLast();


    }
}
