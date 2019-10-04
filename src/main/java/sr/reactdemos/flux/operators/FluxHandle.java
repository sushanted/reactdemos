//Sep 30, 2019
package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class FluxHandle {
    public static void main(final String[] args) {

	// This is similar to create/push but you get an opportunity to intervene into
	// any flux operation
	Flux.range(0, 10)//
		.handle((nextItem, sink) -> {

		    // kind of limit
		    if (nextItem > 5) {
			System.out.println("Completing the sink");
			sink.complete();
		    } else {
			// kind of map
			sink.next(nextItem * (int) sink.currentContext().get("factor"));
		    }
		})//
		.doOnNext(System.out::println)//
		.subscriberContext(context -> context.put("factor", 2))
		.blockLast();
    }
}
