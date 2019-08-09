//Aug 9, 2019
package sr.reactdemos.mono.operators;

import java.time.Duration;

import reactor.core.publisher.Mono;

public class MonoTakeTimeout {
    public static void main(final String[] args) {

	// take vs block(Duration) : take returns empty mono, block returns value

	System.out.println(//
		Mono.just("x")//
			.delayElement(Duration.ofSeconds(2))//
			.take(Duration.ofSeconds(1))// less than the delay so always will return empty Mono
			.block()//
	);

	System.out.println(//
		Mono.just("x")//
			.delayElement(Duration.ofSeconds(2))//
			// take until the other publisher finishes, else return empty mono
			.takeUntilOther(Mono.delay(Duration.ofSeconds(1)))// less than the delay so always will return
									  // empty Mono
			.block()//
	);
    }
}
