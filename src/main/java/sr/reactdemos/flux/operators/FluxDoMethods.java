//Sep 15, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxDoMethods {
    public static void main(final String[] args) {

	System.out.println("\ndoOnTerminate and doAfterTerminate : running after complete");
	// terminate : doOnComplete OR doOnError
	// doAfterTerminate : runs after the final subscriber is complete running
	Flux.just(1, 2, 3)//
		.doOnTerminate(Utils.printRunnable("On terminate1"))//
		.doOnTerminate(Utils.printRunnable("On terminate2"))//
		.doOnTerminate(Utils.printRunnable("On terminate3"))//
		.doAfterTerminate(Utils.printRunnable("After terminate1"))//
		.doAfterTerminate(Utils.printRunnable("After terminate2"))//
		.doAfterTerminate(Utils.printRunnable("After terminate3"))//
		.subscribe(i -> {
		}, //
			Utils.print("The flux has erred, now we'll run the do after terminates"), //
			Utils.printRunnable("The flux has completed, now we'll run the do after terminates")//
		);

	System.out.println("\ndoOnTerminate and doAfterTerminate : running after an error");
	Flux.error(new RuntimeException())//
		.doOnTerminate(Utils.printRunnable("On terminate1"))//
		.doOnTerminate(Utils.printRunnable("On terminate2"))//
		.doOnTerminate(Utils.printRunnable("On terminate3"))//
		.doAfterTerminate(Utils.printRunnable("After terminate1"))//
		.doAfterTerminate(Utils.printRunnable("After terminate2"))//
		.doAfterTerminate(Utils.printRunnable("After terminate3"))//
		.subscribe(i -> {
		}, //
			Utils.print("The flux has erred, now we'll run the do after terminates"), //
			Utils.printRunnable("The flux has completed, now we'll run the do after terminates")//
		);

	System.out.println("\ndoFirst vs doOnSubscribe");
	Flux.just(1, 2)//
		.doFirst(Utils.printRunnable(
			"before subscribing to upstream publisher 1 (the signal is travelling upstream)"))
		.doOnSubscribe(
			Utils.print("after subscribing to upstream publisher 1 (the signal is travelling downstream)"))//
		.doOnSubscribe(
			Utils.print("after subscribing to upstream publisher 2 (the signal is travelling downstream)"))//
		.doFirst(Utils.printRunnable(
			"before subscribing to upstream publisher 2 (the signal is travelling upstream)"))
		.blockLast();


	// doFinally : doOnComplete OR doOnError OR doOnCancel
	System.out.println("\ndoFinally and doOnComplete, doOnError and doOnCancel");
	Flux.just(1, 2)//
		.doOnComplete(Utils.printRunnable("doOnComplete"))//
		.doOnError(Utils.print("doOnError"))
		.doOnCancel(Utils.printRunnable("doOnCancel"))//
		.doFinally(signal -> System.out.println("Completed with " + signal))//
		.blockLast();

	Flux.error(new RuntimeException())//
		.doOnComplete(Utils.printRunnable("doOnComplete"))//
		.doOnError(Utils.print("doOnError"))
		.doOnCancel(Utils.printRunnable("doOnCancel"))//
		.doFinally(signal -> System.out.println("Completed with " + signal))//
		.onErrorReturn(-1)//
		.blockLast();

	final Disposable disposable = Flux.just(1, 2)//
		.delayElements(Duration.ofMillis(100))//
		.doOnComplete(Utils.printRunnable("doOnComplete"))//
		.doOnError(Utils.print("doOnError"))
		.doOnCancel(Utils.printRunnable("doOnCancel"))//
		.doFinally(signal -> System.out.println("Completed with " + signal))//
		.subscribe();

	disposable.dispose();

	// doOnEach : onNext OR onComplete OR onError
	System.out.println("\ndoOnEach");
	Flux.just(1, 2, 3)//
		.doOnNext(i -> {
		    if (i == 2)
			throw new RuntimeException(); // this will be passed to onErrorContinue
		})
		.doOnEach(signal -> System.out.println("Signal passed: " + signal))// not triggered if onErrorContinue
										   // is present downstream
		.onErrorContinue((t, o) -> System.out.println("Error occured for " + o + " continuing"))// comment this
													// to see the
													// onError
													// signal
		.blockLast();

	System.out.println("\ndoOnRequest");
	Flux.range(1, 10)//
		.doOnRequest(l -> System.out.println("Requested " + l + " items."))//
		.limitRate(6, 3)// limit the request to the upstream
		.doOnRequest(l -> System.out.println("Requested " + l + " items."))// unlimited requests
		.blockLast();

	System.out.println("\ndoOnDiscard");
	Flux.range(0, 10)//
		.filter(i -> i % 2 == 0)// retain only even numbers
		.doOnDiscard(Object.class, discarded -> System.out.println("Discarded odd number: " + discarded))//
		.blockLast();

    }
}
