//Aug 13, 2019
package sr.reactdemos.mono.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoThen {
    public static void main(final String[] args) {

	// TODO : move all concat related ops in this, like mergeWith, zip, concat

	// Just convert a regular mono to a Mono<Void>
	System.out.println(Mono.just("x")//
		.then()//
		.block());

	System.out.println(Mono.just("x")//
		.then(Mono.just(4))//
		.block());

	System.out.println(Mono.just("x")//
		// then subscribe to other publisher, then complete
		.thenEmpty(Flux.range(0, 5).doOnNext(System.out::println).then())//
		.block());

	System.out.println(Mono.just("x")//
		// then subscribe to other publisher, then complete
		.thenMany(Flux.range(0, 5).doOnNext(System.out::println))//
		.blockLast());
    }
}
