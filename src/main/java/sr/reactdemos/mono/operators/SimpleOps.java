package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

public class SimpleOps {
    public static void main(final String[] args) {


	System.out.println("Default if empty");
	System.out.println(Mono.justOrEmpty(null).defaultIfEmpty("abc").block());

	System.out.println("Has element");
	System.out.println(Mono.justOrEmpty(null).hasElement().block());

	System.out.println("Ignore element");

	System.out.println(Mono.just("IAmIgnored")//
		.ignoreElement()//
		.doOnEach(System.out::println)//
		.doOnDiscard(String.class, value -> System.out.println("\tDiscarded value: " + value))//
		.block());

	System.out.println(Mono.just("x")//
		.cast(CharSequence.class)//
		.block());

	// uses cast inside, but first check assignability, on failure filters out
	System.out.println(Mono.just("x")//
		.ofType(Integer.class)//
		.block());

	// Create a subscriber in a single line and call methods on it on the same lines
	// Else you need to create the subscriber, then call mono.subscribe and then
	// call methods on the subscriber
	System.out.println("SubscribeWith: ");
	System.out.println(Mono.just("x")//
		.subscribeWith(MonoProcessor.create()).block());



    }
}
