//Sep 13, 2019
package sr.reactdemos.flux.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import reactor.core.publisher.Flux;

public class FluxDistinct {

    public static void main(final String[] args) {

	System.out.println("\nSimple Distinct:");
	Flux.just(7, 1, 2, 3, 6, 7, 0, 2, 4, 5, 6, 1)//
		.distinct()//
		.doOnDiscard(Object.class, FluxDistinct::printDiscardedValue)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nDistinct on key-selector");
	Flux.just(4, 2, 1, 3, 6, 5, 7)//
		.distinct(k -> k / 2)//
		.doOnDiscard(Object.class, FluxDistinct::printDiscardedValue)//
		.doOnNext(System.out::println)//
		.blockLast();

	// An LRU cache can be used as the collection to limit the memory usage
	System.out.println("\nDistinct on key-selector using distinct-collection");
	Flux.just(4, 2, 1, 3, 6, 5, 7)//
		.distinct(k -> k, () -> new LinkedHashSet<>(Arrays.asList(1, 2, 3)))// Already filled with some
										    // values, would be considered as
										    // duplicates
		.doOnDiscard(Object.class, FluxDistinct::printDiscardedValue)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println(
		"\nDistinct on key-selector using distinct-collection and distinct predicate (No duplicate even numbers)");
	Flux.just(1, 4, 2, 1, 3, 6, 6, 5, 7, 4)//
		.distinct(//
			k -> k, //
			ArrayList::new, //
			(list, newValue) -> (newValue % 2 == 0 && list.contains(newValue)) ? false : list.add(newValue), //
			Collection::clear//
		)//
		.doOnDiscard(Object.class, FluxDistinct::printDiscardedValue)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nDistinctUntilChanged");
	Flux.just(1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 3, 3, 2, 2, 1, 1)//
		.distinctUntilChanged()//
		.doOnDiscard(Object.class, FluxDistinct::printDiscardedValue)//
		.doOnNext(System.out::println)//
		.blockLast();

	System.out.println("\nDistinctUntilChanged key-extract and compare");
	Flux.just(1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 3, 3, 2, 2, 1, 1, 4, 4)//
		.distinctUntilChanged(k -> k, (v1, v2) -> Math.abs(v1 - v2) < 2)// then duplicate and v2 need to be
										// discarded
		.doOnDiscard(Object.class, FluxDistinct::printDiscardedValue)//
		.doOnNext(System.out::println)//
		.blockLast();

    }

    private static void printDiscardedValue(final Object discarded) {
	System.out.println("\t\t*" + discarded);
    }

}
