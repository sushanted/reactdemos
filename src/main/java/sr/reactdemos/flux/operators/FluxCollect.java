//Sep 10, 2019
package sr.reactdemos.flux.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;

public class FluxCollect {
    public static void main(final String[] args) {
	// collect list returns a Mono, it is not terminal operation
	System.out.println(//
		"Collected as list: " + //
			Flux.range(1, 10)//
				.collectList()//
				.block()//
	);

	// Just like stream
	System.out.println(//
		"Collected as set: " + //
			Flux.range(1, 10)//
				.collect(Collectors.toSet())//
				.block()//
	);

	// Sorted list
	System.out.println(//
		"Collected as sorted list: " + //
			Flux.just(3, 6, 1, 2, 8)//
				.collectSortedList()//
				.block()//
	);

	// Sorted list
	System.out.println(//
		"Collected as sorted list comparing string value: " + //
			Flux.just(3, 6, 1, 2, 8, 11, 67)//
				.collectSortedList(Comparator.comparing(String::valueOf))//
				.block()//
	);

	// Somewhat like reduce
	System.out.println(//
		"Collected into supplied container with supplied addition method: " + //
			Flux.range(1, 10)//
				.collect(ArrayList::new, Collection::add)//
				.block()//
	);

	// Somewhat like reduce
	System.out.println(//
		"Collected into supplied container with supplied addition method: " + //
			Flux.range(1, 10)//
				.collect(StringBuilder::new, StringBuilder::append)//
				.block()//
	);

	demoCollectMap();

    }

    private static void demoCollectMap() {

	System.out.println(//
		"Collected into map with same value: " + //
			Flux.range(1, 10)//
				.collectMap(k -> k + " ==> ")//
				.block()//
	);

	System.out.println(//
		"Collected into map with derived key and value: " + //
			Flux.range(1, 10)//
				.collectMap(//
					k -> k + "*2", //
					v -> v * 2//
				)//
				.block()//
	);

	System.out.println(//
		"Collected into map with derived key and value: " + //
			Flux.range(1, 10)//
				.collectMap(//
					k -> k + "*2", //
					v -> v * 2, //
					TreeMap::new//
				)//
				.block()//
	);

	// similar to group-by in stream
	System.out.println(//
		"Collected into multi-map with derived key and value (integer division by 2): " + //
			Flux.range(1, 10)//
				.collectMultimap(//
					k -> k / 2
				)//
				.block()//
	);

    }
}
