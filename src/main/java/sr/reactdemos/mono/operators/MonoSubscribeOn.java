//Aug 9, 2019
package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoSubscribeOn {
    public static void main(final String[] args) {
	System.out.println(Mono.just("x")//
		.publishOn(Schedulers.newSingle("publishOn"))// All downstream onNext ops
		// will run on publishOn thread
		.doOnRequest(s -> System.out.println("request : " + Thread.currentThread().getName()))
		.doOnSubscribe(s -> System.out.println("subscribe.2 : " + Thread.currentThread().getName()))
		.doOnNext(s -> System.out.println("next : " + Thread.currentThread().getName()))//
		// All of upstream ops will run on subscribeOn thread
		.subscribeOn(Schedulers.newSingle("subscribeOn"))
		// This will run on main
		.doOnSubscribe(s -> System.out.println("subscribe.1 : " + Thread.currentThread().getName()))
		.block());

    }
}
