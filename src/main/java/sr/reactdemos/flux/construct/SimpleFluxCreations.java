//Aug 20, 2019
package sr.reactdemos.flux.construct;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;

public class SimpleFluxCreations {
    public static void main(final String[] args) {
	// Empty flux
	System.out.println(Flux.empty().blockLast());
	System.out.println(Flux.just().blockLast());

	// Single flux
	System.out.println(Flux.just(1).blockLast());

	// Multi flux
	Flux.just(2, 3, 4).subscribe(System.out::println);

	// From an array
	Flux.fromArray(new Integer[] { 5, 6, 7 }).subscribe(System.out::println);

	// From iterable
	Flux.fromIterable(Arrays.asList(8, 9, 10)).subscribe(System.out::println);

	// Range
	Flux.range(11, 9).subscribe(System.out::println);

	// From stream
	final Flux<Integer> str = Flux.fromStream(Stream.of(20, 21, 22));

	str.subscribe(System.out::println);

	// Stream cannot be reused, following will throw an exception
	// str.subscribe(System.out::println);

	// From stream supplier
	Flux.fromStream(Arrays.asList(23, 24, 25)::stream).subscribe(System.out::println);

	Flux.error(RuntimeException::new).onErrorReturn("error occured!").subscribe(System.out::println);

	System.out.println("\nError when requested: true (error occurs only after first request)");

	demoErrorWhenRequested(true);

	System.out.println("\nError when requested: false (error immediately occurs on subscription)");

	demoErrorWhenRequested(false);

    }

    private static void demoErrorWhenRequested(final boolean errorWhenRequested) {
	final AtomicReference<Subscription> subscriptionHolder = new AtomicReference<>();
	Flux.error(new RuntimeException(), errorWhenRequested)//
		.doOnRequest(req -> System.out.println("Requested!"))//
		.doOnSubscribe(sub -> System.out.println("Subscribed!"))//
		.doOnError(err -> System.out.println("Error occured!"))//
		.onErrorReturn("fallback")
		.subscribe(o -> {
	}, t -> {
	}, () -> {
	}, subscriptionHolder::set);

	System.out.println("Requesting now:");
	subscriptionHolder.get().request(1);
    }
}
