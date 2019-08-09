package sr.reactdemos.mono.operators;

import java.util.Date;

import reactor.core.publisher.Mono;

public class MonoOnMethods {
    public static void main(final String[] args) {

	// Create the mono from the value
	System.out.println("onError returned with: " + //
		Mono.just("x")//
			.doOnNext(t -> {
			    throw new RuntimeException();
			})//
			.onErrorReturn("Alternate value")//
			.block()//
	);

	System.out.println("onError returned with: " + //
		Mono.just("x")//
			.doOnNext(t -> {
			    throw new IllegalArgumentException();
			})//
			.onErrorReturn(RuntimeException.class, "Alternate value when RuntimeException")//
			.block()//
	);

	System.out.println("onError returned with: " + //
		Mono.just("x")//
			.doOnNext(t -> {
			    throw new RuntimeException();
			})// only RuntimeException, not even subclass like IllegalArgumentException
			.onErrorReturn(t -> t.getClass().equals(RuntimeException.class),
				"Alternate value when only IllegalArgumentException")//
			.block()//
	);

	// Create the mono on the fly : another api can be called, the function will be
	// called only when error occurs
	System.out.println("onError resumed with: " + //
		Mono.just("x")//
			.doOnNext(t -> {
			    throw new RuntimeException();
			})//
			.onErrorResume(t -> Mono.just("Dynamically calculated alternate value at " + new Date()))//
			.block()//
	);

	// It's Mono so continue does return null at the end
	System.out.println("onError continued : " + //
		Mono.just("x")//
			.doOnNext(t -> {
			    throw new RuntimeException();
			})// consumer fed with the exception and the value which triggered the exception
			.onErrorContinue((t, o) -> System.out.println("exception:" + t + " value:" + o))//
			.block()//
	);

	// It's Mono so continue does return null at the end
	System.out.println("onError mapped : " + //
		Mono.just("x")//
			.doOnNext(t -> {
			    throw new IllegalArgumentException();
			}).onErrorMap(t -> new RuntimeException(t))//
			.onErrorResume(t -> Mono.just(t.getClass().toString())).block()//
	);

	System.out.println("onError stop : " + //
		Mono.just("x")//
			.doOnNext(t -> {
			    throw new IllegalArgumentException();
			})//
			.onErrorStop()// Nullify the effect of onErrorContinue downstream
			.onErrorContinue((t, o) -> System.out.println("onError stop exception: " + t + " value: " + o))//
			.onErrorReturn("default") // This will take effect
			.block()//
	);

	System.out.println("onError stop : " + //
		Mono.just("x")//
			.flatMap(//
				x -> Mono.just("y")//
					.doOnNext(t -> {
					    throw new IllegalArgumentException();
					})//
					.onErrorStop()// Don't let them onErrorContinue in outer stream
			)// This will not be called because of onErrorStop
			.onErrorContinue((t, o) -> System.out
				.println("onErrorContinue called with:: exception: " + t + " value: " + o))//
			.onErrorReturn("default") // This will take effect
			.block()//
	);

    }


}
