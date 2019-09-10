//Sep 9, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;

public class FluxBlock {
    public static void main(final String[] args) {

	final Flux<Integer> zeroToTen = Flux.range(0, 10);
	System.out.println("First element: " + zeroToTen.blockFirst());
	System.out.println("Last element: " + zeroToTen.blockLast());

	try {
	    zeroToTen.delayElements(Duration.ofMillis(200)).blockFirst(Duration.ofMillis(100));
	}catch(final Exception e) {
	    e.printStackTrace();
	}

	try {
	    zeroToTen.delayElements(Duration.ofMillis(200)).blockLast(Duration.ofMillis(900));
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }
}
