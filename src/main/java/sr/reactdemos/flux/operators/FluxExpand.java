package sr.reactdemos.flux.operators;

import reactor.core.publisher.Flux;

public class FluxExpand {
    public static void main(final String[] args) {

	System.out.println("BFS:");
	Flux.just("1 ", "2 ", "3 ")//
		.expand(x -> Flux.just(x + "L", x + "R"))//
		.limitRequest(20)//
		.subscribe(System.out::println);

	System.out.println("DFS:");
	Flux.just("1 ", "2 ", "3 ")//
		// backtrack on 0
		.expandDeep(x -> x.length() < 5 ? Flux.just(x + "L", x + "R") : Flux.empty())//
		.limitRequest(20)//
		.subscribe(System.out::println);
    }
}
