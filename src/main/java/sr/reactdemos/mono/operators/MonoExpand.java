package sr.reactdemos.mono.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoExpand {
    public static void main(final String[] args) {

	System.out.println("BFS:");
	Mono.just(5)//
		.expand(x -> Flux.just(x - 1, x + 1))//
		.limitRequest(10)//
		.subscribe(System.out::println);

	System.out.println("DFS:");
	Mono.just(2)//
		// backtrack on 0
		.expandDeep(x -> x > 0 ? Flux.just(x - 1, x + 1) : Flux.empty())//
		.limitRequest(10)//
		.subscribe(System.out::println);
    }
}
