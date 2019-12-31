//Nov 7, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.function.Tuples;
import sr.reactdemos.utils.Utils;

public class FluxRepeat {

    public static void main(final String[] args) {

	Utils.demo("Unlimited repeat");
	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("repeat unlimited subscribed"))//
		.repeat()//
		.limitRequest(20)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	Utils.demo("Limited repeat");
	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("repeat limited subscribed"))//
		.repeat(5)//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	Utils.demo("Predicate repeat");
	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("repeat predicate subscribed"))//
		.repeat(() -> false)// No subscribe again
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	Utils.demo("Predicate and limited repeat");
	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("repeat predicate subscribed"))//
		.repeat(4, () -> false)// No subscribe again
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	Utils.demo("Predicate and limited repeat");
	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("repeat predicate subscribed"))//
		.repeat(4, () -> true)// Subscribe always
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	Utils.demo("Repeat when");
	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("repeat when subscribed"))//
		// input flux (Flux<Long>) to this function contains the latest attempt
		// elements'
		// count
		.repeatWhen(f -> f.delayElements(Duration.ofMillis(300)).limitRequest(3))//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	Utils.demo("Repeat when different publisher");
	Flux.range(0, 5)//
		.doOnSubscribe(Utils.print("repeat when subscribed"))//
		// input flux (Flux<Long>) to this function contains the latest attempt
		// elements'
		// count
		.repeatWhen(f -> Mono.just("x"))//
		.doOnNext(Utils.printValue("Emitted"))//
		.blockLast();

	// This is special feature of repeatWhen :if the companion Publisher created by
	// the repeatFactory emits Context as trigger objects
	Utils.demo(
		"Repeat when context passing (Note that if the companion Publisher created by the repeatFactory emits Context as trigger objects, these Context will REPLACE the operator's own Context. )");
	Flux.range(0, 3)//
		.repeatWhen(emittedEachAttempt -> emittedEachAttempt
			.flatMap(//
				e -> Mono.subscriberContext()//
					.map(ctx -> Tuples.of(e, ctx))//
			)//
			.flatMap(t2 -> {
			    System.out.println(t2);
			    final long lastEmitted = t2.getT1();
			    final Context ctx = t2.getT2();
			    final int rl = ctx.getOrDefault("repeatsLeft", 0);
			    if (rl > 0) {
				// /!\ THE ctx.put HERE IS THE ESSENTIAL PART /!\
				return Mono.just(//
					ctx.put("repeatsLeft", rl - 1)//
						.put("emitted", lastEmitted)//
				);
			    } else {
				return Mono.empty();
			    }
			}))//
		// The subscriber can control the number of repeats
		.subscriberContext(Context.of("repeatsLeft", 4))//
		.blockLast();

    }

}
