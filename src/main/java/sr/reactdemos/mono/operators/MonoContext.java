//Aug 9, 2019
package sr.reactdemos.mono.operators;

import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class MonoContext {

    public static void main(final String[] args) {

	// Context flows upstream on subscribing to upstream

	System.out.println("Context: " + Mono.subscriberContext()//
		.subscriberContext(context -> {
		    // This way you can access the context
		    System.out.println("Final context: " + context);
		    return context;
		})//
		.map(x -> x)//
		// another way to modify the context
		.subscriberContext(context -> context.put("B", 2))//
		.map(y -> y)//
		// Way to enrich the existing context with the values
		.subscriberContext(Context.of("A", 1))//
		.block());


	// There could be multiple operations on the returned Mono and the context could
	// be set by the leaf operator
	System.out.println(squarer().subscriberContext(Context.of("value", 2)).block());
	System.out.println(squarer().subscriberContext(Context.of("value", 3)).block());

    }

    public static Mono<Integer> squarer() {

	final AtomicInteger value = new AtomicInteger(0);

	// Consider as if the callable calls a web service with the token in the context
	// and populates the mono
	return Mono.fromCallable(() -> value.get() * value.get())//
		.subscriberContext(context -> {
		    // Consider as if the context contains the auth token
		    value.set(context.get("value"));
		    System.out.println("Final context: " + context);
		    return context;
		});//
    }

}