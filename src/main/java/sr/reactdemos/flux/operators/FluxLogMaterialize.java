//Oct 1, 2019
package sr.reactdemos.flux.operators;

import java.util.logging.Level;

import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

public class FluxLogMaterialize {
    public static void main(final String[] args) {

	System.out.println("Simple logs");
	Flux.range(0, 10)//
		.log()//
		.blockLast();

	System.out.println("\nLog only specified signals");
	Flux.range(0, 10)//
		.log("", Level.WARNING, SignalType.ON_SUBSCRIBE, SignalType.REQUEST)//
		.blockLast();

	System.out.println("\nMaterialize signals : only downstream travelling signals");
	Flux.range(0, 10)//
		.materialize()//
		.doOnNext(System.out::println)//
		.dematerialize()//
		.doOnNext(System.out::println)//
		.blockLast();
    }
}
