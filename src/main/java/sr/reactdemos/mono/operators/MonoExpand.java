package sr.reactdemos.mono.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoExpand {
    public static void main(final String[] args) {

	System.out.println("BFS:");
	Mono.just(" ")//
		.expand(x -> Flux.just(x + "L", x + "R"))//
		.limitRequest(20)//
		.subscribe(System.out::println);

	System.out.println("DFS:");
	Mono.just(" ")//
		// backtrack on 0
		.expandDeep(x -> x.length() < 5 ? Flux.just(x + "L", x + "R") : Flux.empty())//
		.limitRequest(20)//
		.subscribe(System.out::println);
    }
}
