//Sep 15, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxDoMethods {
    public static void main(final String[] args) {

	Flux.just(1, 2)//
		.doOnTerminate(Utils.printRunnable("Normal termination!"))//
		.blockLast();

	Flux.just(1,2)//
		.map(k -> {throw new RuntimeException();})//
		.



    }
}
