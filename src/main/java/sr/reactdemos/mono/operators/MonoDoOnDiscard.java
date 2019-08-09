package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;

public class MonoDoOnDiscard {
    public static void main(final String[] args) {

	/**
	 * Note that the discard can happen in any of the upstream filters, so this
	 * operator needs first argument as class of the discarded value (operators like
	 * map in upstream can change the type of the mono).
	 */

	// Discard in filters

	System.out.println("Output: " + Mono.just("x")//
		.filter("y"::equals)// Discard will happen here, the type of discarded value is String
		.map("z"::equals)//
		.filter(Boolean.TRUE::equals)//
		.doOnDiscard(String.class, value -> System.out.println("discarded string value: " + value))//
		.doOnDiscard(Boolean.class, value -> System.out.println("discarded boolean value: " + value))//
		.block());

	System.out.println("Output: " + Mono.just("x")//
		.filter("x"::equals)//
		.map("z"::equals)//
		.filter(Boolean.TRUE::equals)// Discard will happen here, the type of discarded value is Boolean
		.doOnDiscard(String.class, value -> System.out.println("discarded string value: " + value))//
		.doOnDiscard(Boolean.class, value -> System.out.println("discarded boolean value: " + value))//
		.block());

	// Discard by ignore

	System.out.println(Mono.just("IAmIgnored")//
		.ignoreElement()//
		.doOnDiscard(String.class, value -> System.out.println("Discarded value: " + value))//
		.block());


    }
}
