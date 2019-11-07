//Nov 5, 2019
package sr.reactdemos.flux.operators;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import sr.reactdemos.utils.Utils;

/**
 * publishOn requests n items at a time to the upstream and collects n items
 * emitted from upstream into a queue of size n. The downstream is ran on a
 * different thread and the requests from it are fed from the queue. After
 * lowtide(75%) items have been consumed by the downstream, publishOn requests
 * more lowtide(75%) items to the upstream so that the queue becomes full
 * again.(More details can be found out in FluxLimits.java and
 * FluxBackpressure.java)<br>
 * <br>
 * <b>onBackPressure vs publishOn</b> : no strategy in publishOn to drop and
 * limited requests to upstream : <br>
 * publishOn requests n items from upstream and buffers them in a queue of size
 * n, till the downstream consumes lowtide (default: 75%) items from the buffer,
 * no request is made to the upstream. onBackPressure requests unlimited
 * elements from upstream and buffer them and applies the provided policy on the
 * buffered/new items<br>
 * <br>
 * <b>publishOn vs parallel</b> : publishOn -> thread per subscriber , parallel
 * -> parallel rails for upstream elements : <br>
 * publishOn even if provided with a scheduler with multiple threads, uses a
 * single thread for a single subscriber, all the items from the flux for a
 * single subscriber are processed on a single thread from the scheduler.
 * Multiple subscribers might get different threads from the scheduler.
 **/
public class FluxPublishOn {
    public static void main(final String[] args) throws Exception {

	final Flux<Integer> pub = Flux.range(0, 50)//
		.publishOn(Schedulers.elastic())//
		// This will happen on the main thread
		.doOnSubscribe(Utils.printThreadNameWithMsg("Subscribed on thread"))//
		.doOnNext(n -> Utils.nonReactiveSleepForMillis(2));//

	final CountDownLatch allSubscribersFinished = new CountDownLatch(10);

	// Subscribe multiple times, different subscribers might get different thread
	// from the scheduler
	IntStream.range(0, 10)//
		.forEach(//
			i -> pub//
				.doOnNext(
					Utils.printThreadNameWithMsg("Subscriber " + i + " ran on thread"))//
				.doOnComplete(() -> allSubscribersFinished.countDown())//
				.subscribe()//
		);

	allSubscribersFinished.await();
    }
}
