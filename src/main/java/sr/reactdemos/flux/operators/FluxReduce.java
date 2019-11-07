//Nov 7, 2019
package sr.reactdemos.flux.operators;

import java.util.ArrayList;
import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxReduce {

    public static void main(final String[] args) {

	Utils.demo("Reduce into same type as flux, minimum 2 items required");
	System.out.println(//
		"Sum of first 10 numbers: " + //
			Flux.range(1, 10)//
				.reduce((sum, next) -> sum + next)//
				.block()//
	);

	System.out.println(//
		"Sum of first 1 number: " + //
			Flux.just(1)//
				.reduce((sum, next) -> sum + next)//
				.block()//
	);

	System.out.println(//
		"Sum of first 0 number: " + //
			Flux.<Integer>just()//
				.reduce((sum, next) -> sum + next)//
				.block()//
	);

	Utils.demo("Reduce into different type than flux");
	System.out.println(//
		"First 5 numbers concatenated: " + //
			Flux.range(1, 5)//
				.reduce("", (concat, number) -> concat + "_" + number)//
				.block()//
	);

	System.out.println(//
		"First 5 numbers in list: " + //
			Flux.range(1, 5)//
				.reduce(//
					new ArrayList<Integer>(), //
					FluxReduce::addIntoList//
				)//
				.block()//
	);

	Utils.demo("Reduce with");
	System.out.println(//
		"First 5 numbers in list: " + //
			Flux.range(1, 5)//
				.reduceWith(//
					ArrayList<Integer>::new, //
					FluxReduce::addIntoList//
				)//
				.block()//
	);

    }

    public static <T> ArrayList<T> addIntoList(final ArrayList<T> list, final T number) {
	list.add(number);
	return list;
    }

}
