//Sep 30, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class FluxJoin {

    public static void main(final String[] args) {

	// one character = 50ms
	// 1-
	// ---2-
	// ------3-
	// ---------4-
	// A-
	// --B-
	// ----C-
	// ------D-

	// Temporal join of two flux
	System.out.println("Join 2 flux temporally ");
	Flux.just(1, 2, 3, 4)//
		.delayElements(Duration.ofMillis(150))//
		.doOnNext(Utils.printWithMsg("Born"))//
	.join(//
		      // Another flux : RHS of the join
			Flux.just("A", "B", "C", "D")//
				.delayElements(Duration.ofMillis(100))//
				.doOnNext(Utils.printWithMsg("Born")), //
			// Life time of each LHS emitted element : till the mono emits
			leftEnd -> Mono.just(leftEnd)//
				.delayElement(Duration.ofMillis(100))//
				.doOnNext(Utils.printWithMsg("Died")), //
			// Life time of each RHS emitted element : till the mono emits
			rightEnd -> Mono.just(rightEnd)//
				.delayElement(Duration.ofMillis(100))//
				.doOnNext(Utils.printWithMsg("Died")), //
			// (l,r) is each pair which shared their life-time for some-time
			(l, r) -> l + " AND " + r//
		)//
		.doOnNext(Utils.printWithMsg("Shared life:"))//
		.blockLast();

	System.out.println("\nGroup Join 2 flux temporally ");
	Flux.just(1, 2, 3, 4)//
		.delayElements(Duration.ofMillis(150))//
		.doOnNext(Utils.printWithMsg("Born"))//
		.groupJoin(//
		      // Another flux : RHS of the join
			Flux.just("A", "B", "C", "D")//
				.delayElements(Duration.ofMillis(100))//
				.doOnNext(Utils.printWithMsg("Born")), //
			// Life time of each LHS emitted element : till the mono emits
			leftEnd -> Mono.just(leftEnd)//
				.delayElement(Duration.ofMillis(100))//
				.doOnNext(Utils.printWithMsg("Died")), //
			// Life time of each RHS emitted element : till the mono emits
			rightEnd -> Mono.just(rightEnd)//
				.delayElement(Duration.ofMillis(100))//
				.doOnNext(Utils.printWithMsg("Died")), //
			// (l,fr) is a pair where l sharing its life with r values represented as flux
			// fr
			(l, fr) -> Mono.just(l).map(String::valueOf).concatWith(fr)//
		)//
		.flatMap(f -> f.collectList())//
		.doOnNext(Utils.printWithMsg("Shared life:"))//
		.blockLast();

    }

}
