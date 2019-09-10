//Aug 29, 2019
package sr.reactdemos.flux.construct;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class FluxCombineConcatMergeZip {
    public static void main(final String[] args) {

	// Combine till all of them complete : last element will be combination of last
	// element from each publisher [X,9,9,9,Y]
	// Algorithm :
	// 1. Wait till every publisher emit at-least one element, if multiple values
	// emitted by a publisher before others emit any value, track only latest value
	// 2. Emit latest values from all
	// 3. Wait till any of the publisher emits any value
	// 4. repeat from step 2
	Flux.combineLatest(//
		Arrays::toString, //
		Mono.just("X").delayElement(Duration.ofMillis(500)), //
		Flux.range(0, 10).delaySequence(Duration.ofMillis(600)), // Start late, but emit without delay
		Flux.range(0, 10).delayElements(Duration.ofMillis(200)), //
		Flux.range(0, 10).delayElements(Duration.ofMillis(400)), //
		Mono.just("Y")//
	).doOnNext(System.out::println)//
		.blockLast();

	Flux.concat(//
		Flux.range(0, 11), //
		Flux.range(11, 10), //
		Mono.just("X")//
	).subscribe(System.out::println);

	System.out.println("\nAny errors are immediately forwarded to the downstream");
	Flux.concat(//
		Flux.range(0, 11)//
			.doOnNext(i -> {
			    if (i > 6)
				throw new RuntimeException();
			}), //
		Flux.range(11, 10), //
		Mono.just("This will not be printed!")//
	)//
		.onErrorResume(t -> Mono.just("Error occured!"))//
		.subscribe(System.out::println);

	System.out.println("\nAny errors are forwarded to the downstream after all sources are complete");
	Flux.concatDelayError(//
		Flux.range(0, 11)//
			.doOnNext(i -> {
			    if (i > 6) {
				System.out.println("No more elements from this flux, error occured!");
				throw new RuntimeException();
			    }
			}), //
		Flux.range(11, 10)//
			.doOnNext(i -> {
			    if (i > 15) {
				System.out.println("No more elements from this flux, error occured!");
				throw new RuntimeException();
			    }
			}), //
		Mono.just("COMPLETE")//
	)//
		.onErrorResume(t -> Mono.just("Error occured!"))//
		.subscribe(System.out::println);

	System.out.println("\ndelayUntilEnd");
	Flux.concatDelayError(//
		Flux.just(//
			Flux.range(0, 5)//
				.doOnNext(i -> {
				    if (i > 3) {
					System.out.println("No more elements from this flux, error occured!");
					throw new RuntimeException();
				    }
				})//
				.map(String::valueOf), //
			Mono.just("delayUntilEnd was true")//
		), //
		false, //
		1//
	)//
		.onErrorResume(t -> Mono.just("Error occured!"))//
		.subscribe(System.out::println);

	System.out.println("\nMerge odd and even: publishing simultaneously");
	Flux.merge(//
		Flux.range(0, 10)// Even publisher
			.filter(i -> i % 2 == 0)//
			.delayElements(Duration.ofMillis(100)), //
		Flux.range(0, 10)// Odd publisher
			.filter(i -> i % 2 == 1)//
			.delaySequence(Duration.ofMillis(50))//
			.delayElements(Duration.ofMillis(100))//
	)//
		.doOnNext(System.out::println)// Merged results
		.blockLast();

	System.out.println("\nMerge max concurrency: only 2 publishing simultaneously");
	Flux.merge(Flux.just(//
		Flux.just(0, 0, 0, 0, 0).delayElements(Duration.ofMillis(100)), //
		Flux.just(1, 1, 1, 1, 1).delayElements(Duration.ofMillis(100)), //
		Flux.just(2, 2, 2, 2, 2).delayElements(Duration.ofMillis(100)), //
		Flux.just(3, 3, 3, 3, 3).delayElements(Duration.ofMillis(100))//
	), 2)//
		.doOnNext(System.out::println)// Merged results
		.blockLast();

	System.out.println("\nMerge with error");

	demoDelayError(Flux::merge);

	System.out.println("\nMergeDelayError with error");

	demoDelayError(Flux::mergeDelayError);

	// order latest elements from all the publishers
	// Algorithm :
	// 1. Wait for one element availability from each of n publishers
	// 2. Publish the smallest element
	// 3. Get next element from the publisher from which smallest element was
	// published
	// 4. repeat from 2
	// 5. if the publisher with smallest element completed after the smallest
	// element, repeat from 2 for remaining publishers
	System.out.println("\nMerge ordered");
	Flux.mergeOrdered(//
		Comparator.reverseOrder(), //
		Flux.just(5, 1, 8).delayElements(Duration.ofMillis(100)),
		Flux.just(2, 3, 4).delayElements(Duration.ofMillis(100)), //
		Flux.just(6, 0, 9).delayElements(Duration.ofMillis(100))//
	).doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nMerge ordered example 2");
	Flux.mergeOrdered(//
		Comparator.naturalOrder(), //
		Flux.just(1, 2, 5, 0).delayElements(Duration.ofMillis(100)), Flux.just(3, 4) // Though this is
											     // immediately available,
											     // will wait for other
											     // publisher
	).doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nConcat : LAZY subscription with all publishers");
	demoConcatVsMergeSquential(Flux::concat);

	System.out.println("\nMergeSequential : EAGER subscription with all publishers");
	demoConcatVsMergeSquential(Flux::mergeSequential);

	System.out.println("\nMergeSequential max concurrency: only 2 subscribed simultaneously");
	Flux.mergeSequential(Flux.just(//
		Flux.just(0, 0, 0, 0, 0).delayElements(Duration.ofMillis(1))
			.doOnSubscribe(Utils.print("subscribed to first")), //
		Flux.just(1, 1, 1, 1, 1).delayElements(Duration.ofMillis(1))
			.doOnSubscribe(Utils.print("subscribed to second")), //
		Flux.just(2, 2, 2, 2, 2).delayElements(Duration.ofMillis(1))
			.doOnSubscribe(Utils.print("subscribed to third")), //
		Flux.just(3, 3, 3, 3, 3).delayElements(Duration.ofMillis(1))
			.doOnSubscribe(Utils.print("subscribed to fourth"))//
	), 2, 1)//
		.doOnNext(System.out::println)// Merged results
		.blockLast();

	System.out.println("\nZip Into Tuple : \n\tunlike combine latest, this takes only one element from each source"
		+ "\n\t"
		+ "combine latest may repeat the last elements if no new elements are emitted by other sources");
	Flux.zip(// 4 is ignored : min(length(source)) elements are emitted (until any of the
		 // sources completes)
		Flux.just(1, 2, 3, 4).delayElements(Duration.ofMillis(100)), //
		Flux.just("A", "B", "C"), //
		Flux.just("Å", "ı", "Ç").delayElements(Duration.ofMillis(200))// All tuple creations will wait for
									      // elements from this source
	)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nZip using combinator");
	Flux.zip(//
		oo -> "Combined:" + Arrays.toString(oo), //
		Flux.just(1, 2, 3, 4), //
		Flux.just("A", "B", "C", "D", "E")//
	)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nZip two using combinator");
	Flux.zip(//
		Flux.just(1, 2, 3, 4), //
		Flux.just("A", "B", "C", "D", "E"), //
		(num, str) -> str + num//
	)//
		.doOnNext(System.out::println)//
		.blockLast();


    }

    private static void demoConcatVsMergeSquential(
	    final Function<Iterable<Flux<Integer>>, Flux<Integer>> fluxProvider) {
	fluxProvider.apply(//
		Arrays.asList(//
			Flux.range(0, 10)//
				.delayElements(Duration.ofMillis(1))//
				.doOnSubscribe(Utils.print("subscribed to first")), //
			Flux.range(10, 10)//
				.doOnSubscribe(Utils.print("subscribed to second"))//
		//
		))//
		.doOnNext(System.out::println)//
		.blockLast();
    }

    @SuppressWarnings("unchecked")
    public static void demoDelayError(final BiFunction<Integer, Flux<Integer>[], Flux<Integer>> fluxProvider) {

	fluxProvider.apply(//
		1, //
		new Flux[] { Flux.range(0, 10)//
			.doOnNext(next -> {
			    if (next == 5) {
				System.out.println("Erroring out first publisher!");
				throw new RuntimeException();
			    }
			}), //
			Flux.range(10, 10) }//
	).onErrorReturn(-1) // This value will be printed at the end.
		.doOnNext(System.out::println)// Merged results
		.blockLast();

    }
}
