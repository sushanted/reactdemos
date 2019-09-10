package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;

//TODO : not sure about the usage

public class MonoCheckpoint {

    public static void main(final String[] args) {
	Mono.just("hello")//
		// This will not be printed
		.checkpoint("one", false)//
		.map("x"::concat)//
		.checkpoint("two", false)//
		.map(x -> {
		    if (!x.isEmpty()) {
			throw new RuntimeException();
		    }
		    return "";
		})//
		.checkpoint("three", false)//
		.filter(x -> x.isEmpty())//
		.checkpoint("four", false)//
		.map(x -> "")//
		// This will be printed
		.checkpoint("five", false)//
		.block();

    }
}
