package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;

public class MonoDoOnEach {
    public static void main(final String[] args) {
	Mono.just("x")//
		.doOnEach(System.out::println)//
		.map(x -> 5)
		.doOnEach(System.out::println)//
		.block();

	Mono.empty()//
		.doOnEach(System.out::println)//
		.block();

	Mono.error(RuntimeException::new)//
		.doOnEach(System.out::println)//
		.onErrorResume(t -> Mono.just(""))//
		.block();
    }
}
