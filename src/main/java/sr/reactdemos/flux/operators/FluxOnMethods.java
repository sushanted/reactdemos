//Oct 22, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import sr.reactdemos.utils.Utils;

public class FluxOnMethods {
    public static void main(final String[] args) {

	/**
	 * In attached publish (no publishOn between first publisher and error
	 * operator): In case of error, onNext (emitted) count(e) is not incremented by
	 * the FluxRange and next item is emitted to fulfill the current requested
	 * count, i.e. extra items are emitted by the FluxRange in case of error.
	 *
	 * <br>
	 * tryOnNext : returns false in case of error in onNext of next downstream
	 * operator
	 *
	 * <pre>
	 * boolean b = a.tryOnNext((int) i);
	 *
	 * if (b) {
	 *     e++;
	 * }
	 * </pre>
	 *
	 *
	 */
	System.out.println("\n\nOnErrorContinue : attached publish");
	Flux.range(0, 10)//
		.doOnRequest(Utils.printLongWithMsg("requested"))//
		.map(x -> 120 / (x - 5))//
		.onErrorContinue(Utils::printExceptionAndValue)//
		.doOnNext(Utils.printValue("Emitted"))//
		.limitRequest(6)//
		.blockLast();

	/**
	 * In detached publish (publishOn between first publisher and error operator):
	 * In case of error in downstream (after publishOn), emitted count is not
	 * incremented by the publish-on and thus requests more items from upstream till
	 * error is encountered downstream.
	 *
	 * <br>
	 * tryOnNext : returns false in case of error in onNext of next downstream
	 * operator
	 *
	 * <pre>
	 * if (a.tryOnNext(v)) {
	 *     emitted++;
	 * }
	 * </pre>
	 */
	System.out.println("\n\nOnErrorContinue : detached publish");
	Flux.range(0, 10)//
		.doOnRequest(Utils.printLongWithMsg("requested"))//
		.publishOn(Schedulers.elastic(), 1)//
		.doOnRequest(Utils.printLongWithMsg("requested from main"))//
		.map(x -> 120 / (x - 5))//
		.onErrorContinue(Utils::printExceptionAndValue)//
		.doOnNext(Utils.printValue("Emitted"))//
		.limitRequest(6)//
		.blockLast();

	System.out.println("\n\nOnErrorContinue : specific class error");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//

		.doOnNext(x -> {
		    if (x > 100)
			throw new RuntimeException("Runtime");
		})//
		.onErrorContinue(ArithmeticException.class, Utils.printExceptionAndValue("Arithmetic ::"))//
		// Following will not execute max only one onErrorContinue can be specified for
		// an operator
		.onErrorContinue(RuntimeException.class, Utils.printExceptionAndValue("Runtime ::"))//
		// For non Arithmetic errors following take effect
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\nOnErrorContinue : specific class error");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		.onErrorContinue(ArithmeticException.class, Utils.printExceptionAndValue("Arithmetic ::"))//
		.doOnNext(x -> {
		    if (x > 100)
			throw new RuntimeException("Runtime");
		})//
		  // Following will be executed as it is for different operator
		.onErrorContinue(RuntimeException.class, Utils.printExceptionAndValue("Runtime ::"))//
		// For non Arithmetic errors following take effect
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\nOnErrorContinue : error predicate");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		.doOnNext(x -> {
		    if (x > 100)
			// This will not be processed by onErrorContinue
			throw new RuntimeException("Runtime");
		})//
		.onErrorContinue(//
			t -> t instanceof ArithmeticException, //
			Utils.printExceptionAndValue("Arithmetic ::")//
		)//
		// For non Arithmetic errors following take effect
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\nOnErrorMap");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		.onErrorMap(//
			ArithmeticException.class, //
			// wrap it in runtime
			t -> new RuntimeException("Arithmetic wrapped in runtime")//
		)//
		.doOnError(Utils::print)//
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\nOnErrorMap : on error continue takes over the onErrorMap");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		// This will not be printed as onErrorContinue is specified
		.onErrorMap(//
			ArithmeticException.class, //
			// wrap it in runtime
			t -> new RuntimeException("Arithmetic wrapped in runtime")//
		)//
		 // This will not be printed as onErrorContinue is specified
		.doOnError(Utils::print)//
		// This will nullify the doOnError and onErrorMap
		.onErrorContinue(Utils::printExceptionAndValue)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\nOnErrorResume :");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		.onErrorResume(//
			t -> Mono.just(-1)
		)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\nOnErrorResume :");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		.cast(Object.class)//
		.onErrorResume(//
			Flux::just// Any publisher : Mono, Flux, etc
		)
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\nOnErrorReturn :");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		.onErrorReturn(-1)// Any publisher : Mono, Flux, etc
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\n\nonError stop : not used");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		// .onErrorStop()// <----- This would have prevented any onErrorContinue in
		// downstream
		.onErrorContinue(//
			(t, o) -> System.out.println("onErrorContinue called with:: exception: " + t + " value: " + o)//
		)//
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	System.out.println("\n\n\nonError stop : used");
	Flux.range(0, 10)//
		.map(x -> 120 / (x - 5))//
		.onErrorStop()// <----- This will prevent any onErrorContinue in downstream
		.onErrorContinue(//
			(t, o) -> System.out.println("onErrorContinue called with:: exception: " + t + " value: " + o)//
		)//
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();



    }

}
