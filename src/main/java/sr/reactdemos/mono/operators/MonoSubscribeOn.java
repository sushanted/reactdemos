//Aug 9, 2019
package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoSubscribeOn {
    public static void main(final String[] args) {

	// Every signal after subscribeOn runs on the specified scheduler, till the next
	// occurrence of publishOn

	System.out.println(Mono.just("x")//
		.doOnRequest(s -> System.out.println("request : " + Thread.currentThread().getName()))
		.doOnSubscribe(s -> System.out.println("subscribe : " + Thread.currentThread().getName()))
		.doOnNext(s -> System.out.println("next : " + Thread.currentThread().getName()))//
		// All of upstream ops will run on subscribeOn thread
		.subscribeOn(Schedulers.newSingle("subscribeOn"))
		// This will run on main
		.doOnSubscribe(s -> System.out
			.println("doOnSubscribe:(before subscribe on) : " + Thread.currentThread().getName()))
		.doOnRequest(s -> System.out
			.println("doOnRequest:(before subscribe on) : " + Thread.currentThread().getName()))
		.doOnNext(s -> System.out.println("final next : " + Thread.currentThread().getName()))//
		.block());

	// Publish on affects all the downstream signals
	System.out.println(Mono.just("x")//
		.doOnRequest(s -> System.out.println("request : " + Thread.currentThread().getName()))
		.publishOn(Schedulers.newSingle("publishOn"))// All downstream onNext ops
		.doOnRequest(s -> System.out.println("request : " + Thread.currentThread().getName()))
		.doOnSubscribe(s -> System.out.println("subscribe : " + Thread.currentThread().getName()))
		// will run on publishOn thread
		.doOnNext(s -> System.out.println("next : " + Thread.currentThread().getName()))//
		// All of upstream ops will run on subscribeOn thread
		.subscribeOn(Schedulers.newSingle("subscribeOn"))
		// This will run on main
		.doOnSubscribe(s -> System.out
			.println("doOnSubscribe:(before subscribe on) : " + Thread.currentThread().getName()))
		.doOnRequest(s -> System.out
			.println("doOnRequest:(before subscribe on) : " + Thread.currentThread().getName()))
		.doOnNext(s -> System.out.println("final next : " + Thread.currentThread().getName()))//
		.block());

    }
}
