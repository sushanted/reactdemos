//Aug 8, 2019
package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

public class MonoSignals {
    public static void main(final String[] args) {

	System.out.println("Do on each:");
	Mono.just("x")//
		.doOnEach(signal -> System.out.println("\t" + signal))//
		.subscribe();

	System.out.println(Mono.error(new RuntimeException())//
		.materialize()//
		.map(Signal::getType)//
		.block());

	System.out.println(Mono.just("x")//
		.materialize()//
		.map(Signal::getType)//
		.block());

	System.out.println(Mono.empty()//
		.materialize()//
		.map(Signal::getType)//
		.block());

	System.out.println(Mono.just("x")//
		.materialize()//
		.doOnNext(System.out::println)
		.dematerialize()// Again construct a Mono from signal
		.block());
    }
}
