//Sep 12, 2019
package sr.reactdemos.mono.operators;

import java.time.Duration;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoBlock {

    public static void main(final String[] args) {

	Mono.fromRunnable(() -> Mono.just("x").subscribeOn(Schedulers.parallel()).block())//
		.doOnError(t -> t.printStackTrace())//
		.retryBackoff(300, Duration.ofMillis(200), Duration.ofMillis(200))//
		.block();

	// TODO Auto-generated method stub
	System.out.println(//
		">>" + Mono.fromCallable(//
			() -> {
			    Mono.fromCallable(() -> {
			    System.out.println("Running");
			    return "hello";
			    }).block();
			    throw new Error();
			}//
		)//
			.retryBackoff(300, Duration.ofMillis(200), Duration.ofMillis(200))//
			.block()//
	);
    }

}
