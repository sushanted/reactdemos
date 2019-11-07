//Oct 25, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxOr {
    public static void main(final String[] args) {
	Flux.just("x", "y", "z")//
		.delayElements(Duration.ofMillis(100))//
		.or(// This will emit the first element faster than the original flux and would be
		    // used till the last element(c)
			Flux.just("a", "b", "c")//
				.delayElements(Duration.ofMillis(50))//
		)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();
    }
}
