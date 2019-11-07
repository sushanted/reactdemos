//Aug 6, 2019
package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import sr.reactdemos.utils.Utils;

public class MonoPublishOn {
    public static void main(final String[] args) {

	final Mono<String> mono = Mono.just("x")//
		.doOnSubscribe(Utils.print("subscribed multiple times"));
	// Every use does upstream subscription
	mono.map(x -> "y").subscribe(value -> System.out.println("Mapped value: " + value));
	mono.map(x -> "z").subscribe(value -> System.out.println("Mapped value: " + value));

	System.out.println(Mono.just("x")//
		.doOnSubscribe(Utils.print("subscribed only once"))
		// takes mono as input and gives mono as output
		.publish(m -> {
		    // provide(publish) the mono to multiple functions as input, they can use
		    // without
		    // upstream subscription (instead if I use a variable and store the mono in it
		    // there will be multiple subscription)
		    m.map(x -> "y").subscribe(value -> System.out.println("Mapped value: " + value));
		    m.map(x -> "z").subscribe(value -> System.out.println("Mapped value: " + value));
		    return m;
		})// Directly provide the mono and you can do ops on that
		.block());

	Mono.just("x")//
		.publishOn(Schedulers.newSingle("newT"))//
		.doOnNext(Utils::printThreadName)//
		.block();

	// flatMap provides a new publisher, all next subscribers subscribe to this
	// publisher
	Mono.just("x")//
		.flatMap(//
			x -> Mono.just("y")//
				.publishOn(Schedulers.newSingle("newT"))//
		)//
		.doOnNext(Utils::printThreadName)//
		.block();
    }
}
