//Aug 8, 2019
package sr.reactdemos.mono.operators;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class MonoRepeatRetry {
    public static void main(final String[] args) {

	// repeat returns a flux created from repeated subscriptions

	System.out.println("Repeat forever");
	Mono.just("\tx")//
		.doOnSubscribe(Utils.print("\trepeat unlimited subscribed"))//
		.repeat()// re-subscribe on completion
		.limitRequest(3) // limit it
		.subscribe(System.out::println);

	System.out.println("Repeat 4 times");
	Mono.just("\tx")//
		.doOnSubscribe(Utils.print("\trepeat 4 subscribed"))//
		.repeat(4)// re-subscribe on completion
		.subscribe(System.out::println);

	System.out.println("Repeat predicate");
	Mono.just("\tx")//
		.doOnSubscribe(Utils.print("\trepeat predicate subscribed"))//
		.repeat(4, () -> false)// repeat 4 times or till predicate
		.subscribe(System.out::println);

	System.out.println("Repeat on companion flux output");
	Mono.just("\tcompanion repeat")//
		// input flux to this function contains the latest attempt elements count 0/1
		.repeatWhen(f -> f.limitRequest(3))// till the resultant publisher returns
		.subscribe(System.out::println);

	final AtomicInteger count = new AtomicInteger(0);

	System.out.println("Repeat when empty and companion flux output");
	// Return empty in first two calls then return a value
	Mono.fromCallable(() -> count.incrementAndGet() > 1 ? "\tGot the value after repeat empty" : null)//
		.doOnSubscribe(Utils.print("\trepeating when empty subscribed"))//
		.repeatWhenEmpty(f -> f.limitRequest(7))//
		.subscribe(System.out::println);

	count.set(0);

	System.out.println("Successful retry:");
	Mono.fromCallable(() -> {
	    if (count.incrementAndGet() > 2) {
		return "\tGot the value after retry";
	    }
	    throw new RuntimeException("\tPlease retry");
	})//
		.doOnSubscribe(Utils.print("\tretry subscribed"))//
		.retry(7)// Will not happen till 7 times
		.subscribe(System.out::println);

	System.out.println("Failed retry:");
	Mono.fromCallable(() -> {
	    throw new RuntimeException("\tNot available");
	})//
		.doOnSubscribe(Utils.print("\tretry subscribed"))//
		.retry(2)//
		.doOnError(Utils.print("\tNo success even after multiple retries!"))//
		.subscribe(System.out::println); // This will not be called because error handled by doOnError

	count.set(0);

	System.out.println("Retry on matcher:");
	Mono.fromCallable(() -> {
	    if (count.incrementAndGet() < 4) {
		throw new RuntimeException("\tPlease retry");
	    }
	    throw new RuntimeException("\tSorry");
	})//
		.doOnSubscribe(Utils.print("\tretry subscribed"))//
		.doOnError(e -> System.out.println(e.getMessage()))
		.retry(t -> !t.getMessage().contains("Sorry"))//
		.doOnError(Utils.print("\tNo success even after multiple retries!"))//
		.subscribe(System.out::println);

	// Exponential backoff
	System.out.println("Retry backoff:");
	Mono.error(new RuntimeException())//
		.doOnSubscribe(s -> System.out.println("\tretry subscribed at :" + new Date()))//
		// delay will be never more thast maxBackOff
		// delay will be never less that firstBackOff
		// jitter can applied positive/negative on the value
		.retryBackoff(5, Duration.ofSeconds(1), Duration.ofSeconds(100), 1.0d)//
		.block();

    }

}
