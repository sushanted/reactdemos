//Sep 9, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;
import java.util.Comparator;
import java.util.TreeSet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxBuffer {
    public static void main(final String[] args) {

	System.out.println("\nBuffered list:" + Flux.range(0, 10).buffer().blockFirst());

	System.out.println("\n\n **Time based buffers**");

	System.out
		.println("\nBuffer for every 1 second, open the buffer on first element emittion for current buffer?");
	Flux.range(0, 11)//
		.delayUntil(//
			i -> Mono//
				.delay(Duration.ofMillis(i * 100))//
		)//
		.buffer(Duration.ofSeconds(1))//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println(
		"\nBuffer for every 1 second, open the buffer every .5 second (Overlapping buffers, could be empty if no element emitted in buffer time)");
	Flux.range(0, 20)//
		.delayUntil(//
			i -> Mono//
				.delay(Duration.ofMillis(i * 100))//
		)//
		.buffer(Duration.ofSeconds(1), Duration.ofMillis(500))//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println(
		"\nBuffer for every 1 second, open the buffer every 2 second (Drop elements in the closed time)");
	Flux.range(0, 11)//
		.delayUntil(//
			i -> Mono//
				.delay(Duration.ofMillis(i * 100))//
		)//
		.buffer(Duration.ofSeconds(1), Duration.ofSeconds(2))//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\n\n** Size based buffer **");

	System.out.println("\nBuffered list for every 2 items");
	Flux.range(0, 10)//
		.buffer(2)//
		.doOnNext(System.out::print)//
		.blockLast();

	System.out.println("\nBuffered list for every 2 items out of 3 items batch");
	Flux.range(0, 10)//
		.buffer(2, 3)//
		.doOnNext(System.out::print)//
		.blockLast();

	System.out.println("\nBuffered list for every 3 items overlapping 3-2=1 item");
	Flux.range(0, 10)//
		.buffer(3, 2)//
		.doOnNext(System.out::print)//
		.blockLast();

	System.out.println("\nBuffered list for every 3 items into a reversed TreeSet");
	Flux.range(0, 10)//
		.buffer(3, () -> new TreeSet<>(Comparator.reverseOrder()))//
		.doOnNext(System.out::print)//
		.blockLast();

	System.out.println("\n\n** Time and Size based buffer **");

	System.out.println("\nBuffer max 3 elements or till .5 second");
	Flux.range(0, 10)//
		.delayUntil(//
			i -> Mono//
				.delay(Duration.ofMillis(i * 100))//
		)//
		.bufferTimeout(3, Duration.ofMillis(500))//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\n\n** Conditional buffers  **");

	System.out.println("\nBuffer until an odd number which is divisible by 3, include the element in the buffer");
	Flux.range(0, 12)//
		.bufferUntil(x -> x % 3 == 0 && x % 2 != 0)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println(
		"\nBuffer until an odd number which is divisible by 3, DON'T include the element in current buffer (include it in the next buffer)");
	Flux.range(0, 12)//
		.bufferUntil(x -> x % 3 == 0 && x % 2 != 0, true)// cut-before is true
		.doOnNext(System.out::println)//
		.blockLast();

	// TODO : onDiscard didn't work for this
	System.out.println("\nBuffer while the predicate is true, exclude the false element");
	Flux.range(0, 12)//
		.bufferWhile(x -> x % 3 != 0)//
		.doOnDiscard(Object.class, discarded -> System.out.println("Discarding false element:" + discarded))//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\n\n** companion based buffer **");

	System.out.println("\nBuffer on companion publisher emission, completes if companion completes first ");
	Flux.range(0, 10)//
		.delayElements(Duration.ofMillis(50))//
		.buffer(Flux.range(0, 3).delayElements(Duration.ofMillis(110)))//
		.doOnNext(System.out::print)//
		.blockLast();

	System.out.println(
		"\nBuffer based on companion start and end publishers, end companion depends on the start value");
	Flux.range(0, 20)//
		.delayElements(Duration.ofMillis(50))//
		.bufferWhen(//
			Flux.range(0, 10).delayElements(Duration.ofMillis(100)), // trigger start of the buffer every
										 // 100ms, provide values to end buffer
			openingNumber -> Mono.delay(Duration.ofMillis(openingNumber * 100))// Gradually increasing
											   // buffer window
		)//
		.doOnNext(System.out::println)//
		.blockLast();

    }

}
