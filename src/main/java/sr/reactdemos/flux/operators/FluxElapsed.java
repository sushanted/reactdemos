//Sep 11, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;
import reactor.core.publisher.Flux;

public class FluxElapsed {
    public static void main(final String[] args) {

	System.out.println(
		"Time elapsed from last emitted item, first duration is measured between the subscription and the first element");
	Flux.range(0, 10)//
		.delayElements(Duration.ofMillis(200))//
		.elapsed()//
		.doOnNext(System.out::println)//
		.blockLast();

    }
}
