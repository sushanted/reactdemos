//Jul 24, 2019
package sr.reactdemos.mono.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import sr.reactdemos.utils.Utils;

public class MonoFlatMap {
    public static void main(final String[] args) {

	System.out.println("Flat Map");
	System.out.println((Mono.just("x")//
		.flatMap(x -> Mono.just((int) x.charAt(0))))//
			.block());

	System.out.println("Flat Map Many");
	Mono.just(1)//
		.flatMapMany(x -> Flux.just(x + 1, x + 2, x + 3))//
		.subscribe(System.out::println);

	System.out.println("Flat Map Many multiple signals");
	Mono.just(1)//
		.flatMapMany(//
			x -> Flux.just(x + 1, x + 2, x + 3), // onNext
			t -> Mono.just(-1), // onError
			() -> Mono.just(100)// onComplete
		)//
		.subscribe(System.out::println);

	System.out.println("Flat Map Many multiple signals including error");
	Mono.error(RuntimeException::new)//
		.flatMapMany(//
			x -> Flux.just(1, 2, 3), // onNext
			t -> Mono.just(-1), // onError
			() -> Mono.just(100)// onComplete
		)//
		.subscribe(System.out::println);

	System.out.println("flatMap provides a new publisher, all next subscribers subscribe to this publisher");
	// flatMap provides a new publisher, all next subscribers subscribe to this
	// publisher
	Mono.just("x")//
		.flatMap(x -> Mono.just("y").publishOn(Schedulers.newSingle("newT")))//
		.doOnNext(Utils::printThreadName)//
		.block();

	// FlatMap (or any other operator which (may) provides another Mono) does call
	// onSubscribe immediately on the subscribers, before
	// subscribing
	// to upstream publishers, then subscribes with upstream publishers.
	// Once it gets value emitted from the upstream publisher, it subscribes on the
	// inner publisher, after inner publisher emits value it calls onNext on
	// downstream subscribers.
	System.out.println("\nSubscription test:");
	Mono.just("x")//
		.doOnSubscribe(Utils.print("\toriginal subscribed"))//
		.doOnNext(Utils.print("\toriginal emitted"))//
		.flatMap(//
			x -> Mono.just("y")//
				.doOnSubscribe(Utils.print("\tinner subscribed"))//
				.doOnNext(Utils.print("\tinner emitted"))//
		)//
		.doOnSubscribe(Utils.print("\tderived.1 subscribed"))//
		.doOnNext(Utils.print("\tderived.1 emitted"))//
		.map(y -> "z")//
		.doOnSubscribe(Utils.print("\tderived.2 subscribed"))//
		.doOnNext(Utils.print("\tderived.2 emitted"))//
		.block();


    }

}
