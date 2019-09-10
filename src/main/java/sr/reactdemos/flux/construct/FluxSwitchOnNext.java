//Sep 3, 2019
package sr.reactdemos.flux.construct;

import java.time.Duration;

import reactor.core.publisher.Flux;

public class FluxSwitchOnNext {
    public static void main(final String[] args) {
	Flux.switchOnNext(//
		Flux.just(//
			Flux.range(0, 5)// will start emiting from this till 300ms
				.delayElements(Duration.ofMillis(100)), //
			Flux.range(5, 5)//
		)//
			.delayElements(Duration.ofMillis(300))// As this will be available after 300ms, previous flux
							      // will be switched with this.
	)//
		.doOnNext(System.out::println)//
		.blockLast();

    }
}
