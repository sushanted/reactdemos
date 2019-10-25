//Sep 20, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class FluxFlatMap {
    public static void main(final String[] args) {

	// By default concurrency is 256 and prefetch is 32
	System.out.println("\nFlat map with publisher mapper (no ordering, interleaving)");
	Flux.just("A", "B", "C")//
		.index()//
		.flatMap(//
			indexed -> Flux.range(1, 3)//
				.delayElements(Duration.ofMillis(indexed.getT1()))//
				.map(String::valueOf)//
				.map(num -> indexed.getT2() + num)//
				.doOnSubscribe(Utils.print("Subscribed to " + indexed))//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nFlat map with publisher mapper (no ordering, interleaving), with provided concurrency");
	Flux.just("A", "B", "C", "D")//
		.index()//
		.doOnRequest(Utils.printLongWithMsg("requested by flatmap"))
		.flatMap(//
			indexed -> Flux.range(1, 3)//
				.delayElements(Duration.ofMillis(indexed.getT1()))//
				.map(String::valueOf)//
				.map(num -> indexed.getT2() + num)//
				.doOnSubscribe(Utils.print("Subscribed to " + indexed)), //
			2//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println(
		"\nFlat map with publisher mapper (no ordering, interleaving), with provided concurrency and prefetch");
	Flux.just("A", "B", "C", "D")//
		.index()//
		.doOnRequest(Utils.printLongWithMsg("requested by flatmap"))
		.flatMap(//
			indexed -> Flux.range(1, 5)//
				.delayElements(Duration.ofMillis(indexed.getT1()))//
				.map(String::valueOf)//
				.map(num -> indexed.getT2() + num)//
				.doOnSubscribe(Utils.print("Subscribed to " + indexed))//
				.doOnRequest(
					i -> System.out.println("Requested " + i + " items from inner " + indexed)),
			2, //
			3//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println(
		"\nFlat map delay error with publisher mapper (no ordering, interleaving), with provided concurrency and prefetch");
	try {
	    Flux.just("A", "B", "C", "D")//
		    .flatMapDelayError(//
			    outer -> (outer.equals("A") || outer.equals("C")) ? //
				    Mono.error(new RuntimeException(outer)) : //
				    Flux.range(0, 5)//
					    .map(i -> outer + i),
			    3, // TODO: research: why If more than 'concurrency' outers throw error then this
			       // flatmap hangs (try making this value 2)
			    3//
		    )//
		    .doOnNext(System.out::println)//
		    .blockLast();
	} catch (final Exception e) {
	    System.err.println("This will be printed at the end, all other values will be printed before");
	    e.printStackTrace();
	}

	System.out.println("\nFlat map with multiple mappers");
	Flux.range(-5, 10)//
		.map(i -> 10 / i)//
		.flatMap(//
			Mono::just, //
			Mono::just, //
			() -> Flux.just(100, 99)//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nFlat map iterable exactly same as concatMapIterable, sequncial, no interleaving");
	Flux.range(0, 5)//
		.flatMapIterable(num -> Arrays.asList(num, num + 1, num - 1))//
		.doOnNext(System.out::println)//
		.blockLast();

	final ArrayList<String> list = new ArrayList<String>() {
	    @Override
	    public java.util.Iterator<String> iterator() {
		final Iterator<String> iterator = super.iterator();
		return new Iterator<String>() {

		    @Override
		    public String next() {
			System.out.println("next called");
			return iterator.next();
		    }

		    @Override
		    public boolean hasNext() {
			return iterator.hasNext();
		    }
		};
	    };
	};

	IntStream.range(0, 40)//
		.mapToObj(String::valueOf)//
		.forEach(list::add);

	System.out.println("\nFlat map iterable exactly same as concatMapIterable, sequencial, no interleaving");
	Flux.range(0, 5)//
		// TODO : not sure about the prefetch value here
		.flatMapIterable(num -> list, 20)//
		.doOnNext(System.out::println)//
		.blockLast();


	System.out.println(
		"\nFlat map sequential, the subscription to inner is not sequential but items are queued till completion of earlier inner");
	Flux.just("A", "B", "C")//
		.index()//
		.flatMapSequential(//
			indexed -> Flux.range(1, 3)//
				.delayElements(Duration.ofMillis(indexed.getT1()))//
				.map(String::valueOf)//
				.map(num -> indexed.getT2() + num)//
				.doOnSubscribe(Utils.print("Subscribed to " + indexed))//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println(
		"\nFlat map sequential with concurrency, the subscription to inner is not sequential but items are queued till completion of earlier inner");
	Flux.just("A", "B", "C")//
		.index()//
		.flatMapSequential(//
			indexed -> Flux.range(1, 3)//
				.delayElements(Duration.ofMillis(indexed.getT1()))//
				.map(String::valueOf)//
				.map(num -> indexed.getT2() + num)//
				.doOnSubscribe(Utils.print("Subscribed to " + indexed)), //
			// max 2 inners subscribed to concurrently
			2//
		)//
		.doOnNext(System.out::println)//
		.blockLast();

    }
}
