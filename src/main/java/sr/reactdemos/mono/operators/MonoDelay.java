package sr.reactdemos.mono.operators;

import java.time.Duration;

import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class MonoDelay {
    public static void main(final String[] args) {

//Note that the scheduler on which the mono chain continues execution will
	// be the scheduler provided if the mono is valued, or the current scheduler if
	// the mono completes empty or errors.

	Mono.just("x")//
		// runs on another thread
		.delayElement(Duration.ofSeconds(2))//
		.doOnSuccess(l -> System.out.println(Thread.currentThread().getName()))//
		.block();

	Mono.empty()//
		// runs on same thread : main
		.delayElement(Duration.ofSeconds(2))//
		.doOnTerminate(() -> System.out.println(Thread.currentThread().getName()))//
		.block();

	Mono.error(RuntimeException::new)//
		// runs on same thread : main
		.delayElement(Duration.ofSeconds(2))//
		.doOnTerminate(() -> System.out.println(Thread.currentThread().getName()))//
		.onErrorResume(t -> Mono.just(""))//
		.block();

	Mono.just("x")//
		// Wait for the publisher to complete, maybe post the value to some other
		// web-service
		.delayUntil(value -> Mono.fromRunnable(() -> Utils.sleepForSeconds(2)))//
		.doOnTerminate(() -> System.out.println(Thread.currentThread().getName()))//
		.block();
    }
}
