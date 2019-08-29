//Aug 9, 2019
package sr.reactdemos.mono.operators;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

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

	System.out.println(//
		Mono.just("x")//
			.delayElement(Duration.ofSeconds(2))//
			.timeout(Duration.ofSeconds(1))// less than the delay so always will timeout
			.onErrorReturn(TimeoutException.class, "Timeout occured")//
			.block()//
	);

	System.out.println(//
		Mono.just("x")//
			.delayElement(Duration.ofSeconds(2))//
			.timeout(Mono.delay(Duration.ofSeconds(1)))// less than the delay so always will timeout
			.onErrorReturn(TimeoutException.class, "Timeout occured")//
			.block()//
	);

	System.out.println(//
		Mono.just("x")//
			.delayElement(Duration.ofSeconds(2))//
			// Fallback to new value when times out
			.timeout(Duration.ofSeconds(1), Mono.just("y"))// less than the delay so always will timeout
			.block()//
	);
    }
}
