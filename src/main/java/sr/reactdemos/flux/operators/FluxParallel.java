//Oct 25, 2019
package sr.reactdemos.flux.operators;

import java.util.ArrayList;
import java.util.Collection;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import sr.reactdemos.utils.Utils;

public class FluxParallel {

    public static void main(final String[] args) {

	/**
	 * <pre>
	        |
	        |
	       /|\
	      | | |
	      | | |
	      | | |
	      \ | /
	        |
	        |
	 * </pre>
	 */


	System.out.println("\n\n Parallel flux: ");
	Flux.range(0, 10)//
		.parallel()//
		.doOnNext(Utils.printValue("Original emitted:"))//
		.runOn(Schedulers.elastic())//
		// Now there would be parallel rails processing each item from the flux, each
		// rail will have it's own thread
		.doOnNext(Utils.printValue("Parallel Rail emitted"))//
		// This will be printed multiple times, one per each rail
		.doOnComplete(Utils.printRunnable("Rail completed"))//
		.sequential()// Merge the rails into single, order not guaranteed
		// Now there will be a single thread
		// .doOnNext(Utils::printThreadNameAny)// Runs on a single thread
		.doOnNext(Utils.printValue("Sequencial emitted"))//
		.doOnComplete(Utils.printRunnable("Flux completed"))//
		.blockLast();

	System.out.println("\n\n Parallel flux with provided parallelism: ");
	Flux.range(0, 10)//
		.parallel(2)//
		.doOnNext(Utils.printValue("Original emitted:"))//
		.runOn(Schedulers.elastic())//
		// Now there would be parallel rails processing each item from the flux, each
		// rail will have it's own thread
		.doOnNext(Utils.printValue("Parallel Rail emitted"))//
		// This will be printed multiple times, one per each rail
		.doOnComplete(Utils.printRunnable("Rail completed"))//
		.sequential()// Merge the rails into single
		// Now there will be a single thread
		.doOnNext(Utils.printValue("Sequencial emitted"))//
		.doOnComplete(Utils.printRunnable("Flux completed"))//
		.blockLast();

	System.out.println("\n\n Parallel flux with provided parallelism and prefetch: ");
	Flux.range(0, 10)//
		.doOnRequest(Utils.printLongWithMsg("requested original"))
		.parallel(3, 3)// fetch items in a batch of 3 from source
		.doOnNext(Utils.printValue("Original emitted:"))//
		.runOn(Schedulers.elastic())//
		// Now there would be parallel rails processing each item from the flux, each
		// rail will have it's own thread
		.doOnNext(Utils.printValue("Parallel Rail emitted"))//
		// This will be printed multiple times, one per each rail
		.doOnComplete(Utils.printRunnable("Rail completed"))//
		.sequential()// Merge the rails into single
		// Now there will be a single thread
		.doOnNext(Utils.printValue("Sequencial emitted"))//
		.doOnComplete(Utils.printRunnable("Flux completed"))//
		.blockLast();

	System.out.println("\n\n Parallel flux collected separately: ");
	Flux.range(0, 10)//
		.parallel(3)//
		.doOnNext(Utils.printValue("Original emitted:"))//
		.runOn(Schedulers.elastic())//
		// Now there would be parallel rails processing each item from the flux, each
		// rail will have it's own thread
		.doOnNext(Utils.printValue("Parallel Rail emitted"))//
		// This will be printed multiple times, one per each rail
		.doOnComplete(Utils.printRunnable("Rail completed"))//
		.collect(ArrayList::new, Collection::add)//
		.sequential()// Merge the rails into single, order not guaranteed
		// Now there will be a single thread
		// .doOnNext(Utils::printThreadNameAny)// Runs on a single thread
		.doOnNext(Utils.printValue("Sequencial emitted"))//
		.doOnComplete(Utils.printRunnable("Flux completed"))//
		.blockLast();

    }

}
