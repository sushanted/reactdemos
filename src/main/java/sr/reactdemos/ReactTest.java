//Sep 27, 2019
package sr.reactdemos;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class ReactTest {

    public static void main(final String[] args) {
	System.out.println(Flux.range(0, 9)//
		.map(m -> Mono.just("y").block())//
		.doOnNext(n -> Utils.sleepForMillis(100))
		.blockLast());

    }

}
