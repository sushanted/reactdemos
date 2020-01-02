//Dec 31, 2019
package sr.reactdemos.flux.operators;

import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;
import sr.reactdemos.utils.Utils.TimeTracker;

public class FluxRetry {
    public static void main(final String[] args) {

	// demoRetryUntilSuccess();

	// demoRetryNTimes();

	// demoRetryPredicate();

	// TODO : retry backoff
	// TODO : retry times and predicate
	// TODO : retry when

    }

    private static void demoRetryPredicate() {
	Utils.demo("Retry predicate");

	final TimeTracker timeTracker = new TimeTracker();

	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("Subscribed"))//
		.flatMap(i -> Mono.just(i).filter(v -> v != 2).single())//
		.retry(t -> timeTracker.elapsed() < 400)//
		.doOnError(Utils.print("Retries exhausted!!"))//
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Emitted: "))//
		.blockLast();
    }

    private static void demoRetryNTimes() {
	Utils.demo("Retry n times");

	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("Subscribed"))//
		// single expects an element but filter might make it empty
		.flatMap(i -> Mono.just(i).filter(v -> v != 2).single())//
		.doOnError(Utils.print("Error occured, will retry"))//
		.retry(3)//
		.doOnError(Utils.print("Retries exhausted!!"))//
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Emitted: "))//
		.blockLast();
    }

    private static void demoRetryUntilSuccess() {
	Utils.demo("Retry until success");

	final AtomicInteger errorAt = new AtomicInteger(0);

	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("Subscribed"))//
		// Move the erroneous number ahead by 1
		.flatMap(i -> errorAt.compareAndSet(i, i + 1) ? Mono.error(new RuntimeException()) : Mono.just(i))//
		.doOnError(Utils.print("Error occured, will retry"))//
		.retry()//
		.doOnNext(Utils.printValue("Emitted: "))//
		.doOnComplete(Utils.printRunnable("SUCCESS!!!"))//
		.blockLast();
    }
}
