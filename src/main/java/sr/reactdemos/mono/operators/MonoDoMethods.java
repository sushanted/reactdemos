package sr.reactdemos.mono.operators;

import java.time.Duration;

import reactor.core.publisher.Mono;

public class MonoDoMethods {
    public static void main(final String[] args) {

	// delayedMono initiates subscription with secondMono, then secondMono
	// initiates subscription with firstMono, then firstMono initiates
	// subscription to data. Before any subscription, doFirst is called. After
	// each subscription doOnSubscribe is called.

	final Mono<String> firstMono = chain(Mono.just("data"), "first");//

	final Mono<String> secondMono = chain(firstMono.map("modified "::concat), "second");//

	final Mono<String> delayedMono = chain(secondMono.delayElement(Duration.ofSeconds(2)), "delayed");

	System.out.println("Blocked output: " + delayedMono//
		.block());


    }

    public static <T> Mono<T> chain(final Mono<T> mono, final String monoName) {
	// doFirst will be called in reverse order as it is a call on the publisher
	return mono.doFirst(() -> System.out.println("Before subscribing to " + monoName))//
		.doOnSubscribe(s -> System.out.println("Subscribed to " + monoName))//
		// doOnRequest will be called in reverse order as it is a call on the publisher
		.doOnRequest(l -> System.out.println(monoName + " got request of " + l + " items"))//
		.doOnNext(value -> System.out.println(monoName + " next item: " + value))//
		// This runs before doOnTerminate (irrespective of the position in the chain)
		.doAfterTerminate(() -> System.out.println(monoName + " after termination"))
		.doOnTerminate(() -> System.out.println(monoName + " terminated"))//
	;
    }

}
