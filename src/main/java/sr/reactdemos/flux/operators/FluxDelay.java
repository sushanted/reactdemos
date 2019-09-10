//Sep 9, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxDelay {
    public static void main(final String[] args) {
	Flux.create(emitter -> {
	    Utils.sleepForMillis(100);
	    emitter.next(100);
	    Utils.sleepForMillis(200);
	    emitter.next(200);
	    Utils.sleepForMillis(500);
	    emitter.next(500);
	    emitter.complete();
	})//
		.doOnNext(next -> System.out.println("Emitted: " + next + "" + System.currentTimeMillis()))
		.delayElements(Duration.ofMillis(2000))// Every element is delayed after emittion
		.doOnNext(next -> System.out.println("Delayed: " + next + "" + System.currentTimeMillis()))//
		.blockLast();

    }
}
