//Oct 1, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import sr.reactdemos.utils.Utils;

/**
 * Back-pressure comes into play when lesser items are requested by the
 * downstream than provided by the upstream. The back-pressure operator tracks
 * the request(n) coming to it from downstream and also the onNext() called on
 * it from upstream. An example implementation of the operator could be thought
 * like:
 * <li>increment the request count by n on every request(n) call from downstream
 *
 * <li>decrement the request count by 1 for every onNext() call done on
 * downstream
 * <li>for every onNext() call to the operator from upstream, check if pending
 * request count > 0 ,<br>
 * IF yes : call onNext() on downstream ELSE apply back-pressure strategy
 *
 * <br>
 * <br>
 * Back-pressure operators with buffering are backed by a queue. The onNext()
 * call on these operators involves following sequence:
 * <ol>
 * <li>Call offer on the queue with the element provided in onNext(element)
 * <li>The offer may fail in case the queue is full, the discard policy will be
 * applied in this condition (e.g. discard the latest/oldest element; throw an
 * error)
 * <li>A call to drain the queue is made, in which onNext of the downstream
 * operator is called in a loop, till the current downstream pending requests
 * are satisfied OR queue becomes empty. <br>
 * <b>Note that the onNext on downstream is called in the same thread in which
 * onNext was called on the back-pressure operator, if there is any delay
 * downstream in the execution of each onNext, the queue drain loop will be
 * delayed for each increment. In this case, though the upstream has more
 * elements to offer to the back-pressure operator, the thread will be busy in
 * the drain loop, the upstream will get piled up with the newly received items
 * (imagine a queue Q between the upstream publisher and an another thread which
 * actually produce the item and puts into the queue), the back-pressure looks
 * to be not working (but actually it is not getting chance due to slow
 * same-thread subscriber, and stuck in the onNext():drain():{down.onNext()*}
 * call stack, effectively
 * <ol>
 * <li>for each onNext call from upstream put the item in the queue
 * <li>call drain which calls onNext on downstream with the queued item
 * <li>call to downstream introduces delay in the processing of the item
 * <li>call to downstream finishes
 * <li>call to drain and onNext finishes
 * <li>NOW upstream gets chance to call onNext on the back-pressure operator
 * with the next element
 * <li>the whole process repeats and due to delay in each item processing in the
 * downstream, the upstream queue Q might get full/ cause OOM, <u>though there
 * are enough items available to publish from upstream, as onNext can be called
 * with only one item on the back-pressure operator, only one item gets put in
 * the back-pressure operator queue, only one item gets drained from the
 * back-pressure operator queue and onNext is called on slow processing
 * downstream with the same one item (all in the same thread) </u>
 * </ol>
 * )<br>
 * Solution : if a publishOn is used with a different Scheduler at the immediate
 * downstream of back-pressure operator, all the work in the downstream would be
 * done on different scheduler, the onNext from back-pressure operator on
 * downstream will finish faster and asynchronously, the slow downstream would
 * request items to the back-pressure operator with its slow pace, while the
 * upstream fast publisher would be able to call more number of onNext on
 * back-pressure operator and back-pressure operator would be able to queue more
 * items and also apply discard policy upon the queue full condition.</b>
 * </ol>
 *
 */
public class FluxBackpressure {
    public static void main(final String[] args) {

	System.out.println("back-pressure on a single thread : not effective");
	Flux.range(0, 10)//
		// onNext(item) will be blocked on next downstream operator till the delaying
		// subscriber finishes
		// Though unbounded, will receive items 200 ms apart
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed after every 200 ms
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureBuffer()//
		// .publishOn(Schedulers.elastic())// Note this important part : decouple
		// pub-sub
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t3 -> Utils.sleepForMillis(200))// Make the subscriber thread slow,
		.limitRate(1, 0)// The rate is 1 per 200 ms
		.blockLast();

	System.out.println(
		"\nBack-pressure with slow subscribers working on different thread : working effectively : infinite buffer");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureBuffer()//
		.publishOn(Schedulers.single())// Note this important part : decouple
		// pub-sub
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t1 -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.limitRate(1, 0)// The rate is 1 per 200 ms
		.blockLast();

	System.out.println("\n\n ONLY SIZE based");

	System.out.println("\nBack-pressure with max buffer size : errors on overflow");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureBuffer(3)//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t2 -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();

	System.out
		.println("\nBack-pressure with max buffer size, drop latest: will print only oldest 3 buffered items");
	demoBufferOverflow(BufferOverflowStrategy.DROP_LATEST);

	System.out
		.println("\nBack-pressure with max buffer size, drop oldest: will print only latest 3 buffered items");
	demoBufferOverflow(BufferOverflowStrategy.DROP_OLDEST);

	System.out.println("\nBack-pressure with max buffer size, error : will print -1 i.e. error occured");
	demoBufferOverflow(BufferOverflowStrategy.ERROR);

	System.out.println(
		"\nBack-pressure with max buffer size and consumer : will print the overflowed item \n\t Error is delayed till all items consumed by subscriber");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureBuffer(3, Utils.printValue("Overflowed item"))//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();

	System.out.println(
		"\nBack-pressure with max buffer size, consumer and drop latest");
	demoBackpressureWithConsumerAndStrategy(BufferOverflowStrategy.DROP_LATEST);

	System.out.println("\nBack-pressure with max buffer size, consumer and drop oldest");
	demoBackpressureWithConsumerAndStrategy(BufferOverflowStrategy.DROP_OLDEST);

	System.out.println("\nBack-pressure with max buffer size, consumer and error");
	demoBackpressureWithConsumerAndStrategy(BufferOverflowStrategy.ERROR);

	System.out.println("\n\nTIME AND SIZE based");

	System.out
		.println(
			"\nBack-pressure time and size based : timeout : every item would be kept buffered till ttl (measured from its put into buffer)");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.delayElements(Duration.ofMillis(50))
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.timestamp()//
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureBuffer(Duration.ofMillis(600), 12,
			n -> System.out.println("At: " + System.currentTimeMillis() + ", Overflowed After: "
				+ (System.currentTimeMillis() - n.getT1())))//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();

	System.out.println("\nBack-pressure time and size based : maxSize reached : oldest items would be discarded");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureBuffer(Duration.ofMinutes(5), 5, Utils.printValue("Overflowed item"))//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();

	System.out.println("\n\nDROP on back-pressure");

	System.out.println("\n\nDROP any new items on back-pressure : no request from downstream");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureDrop()//
		.doOnDiscard(Object.class, Utils.printValue("Discarded"))//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();

	System.out.println(
		"\n\nDROP any new items on back-pressure : no request from downstream : hand the dropped item to the consumer");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureDrop(Utils.printValue("Dropped"))//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();

	System.out.println("\n\nERROR : no request from downstream : hand the dropped item to the consumer");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureError()//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();

	System.out.println(
		"\n\nLATEST : only buffer single last element : same as : onBackpressureBuffer(1, BufferOverflowStrategy.DROP_OLDEST)");
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureLatest()//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();

    }

    private static void demoBackpressureWithConsumerAndStrategy(final BufferOverflowStrategy overflowStrategy) {
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureBuffer(3, Utils.printValue("Overflowed item"), overflowStrategy)//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();
    }

    private static void demoBufferOverflow(final BufferOverflowStrategy overflowStrategy) {
	Flux.range(0, 10)//
		// onNext(item) will return fast as soon as handing over the item to subscriber
		// thread
		.doOnRequest(Utils.printLongWithMsg("Requested by backpressure operator"))//
		// this will be printed without any delay
		.doOnNext(Utils.printValue("Upstream Flux range produced"))//
		.onBackpressureBuffer(3, overflowStrategy)//
		.doOnRequest(Utils.printLongWithMsg("Requested to back-pressure operator by downstream"))//
		.publishOn(Schedulers.single(), 1)// Note this important part : decouple
		// pub-sub; request 1 item upstream at a time so slow buffer overflow should
		// occur
		// if 1 not provided, default 256 value is sent upstream
		.onErrorReturn(-1)//
		.doOnNext(Utils.printValue("Downstream received"))//
		.doOnNext(t -> Utils.nonReactiveSleepForMillis(200))// Make the subscriber thread slow,
		.blockLast();
    }

}
