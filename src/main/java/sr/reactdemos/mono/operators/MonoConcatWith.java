package sr.reactdemos.mono.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoConcatWith {
  public static void main(final String[] args) {
    Mono.just(0).concatWith(Flux.range(1, 4)).subscribe(System.out::println);

    Mono.just(0).concatWith(Mono.just(1)).subscribe(System.out::println);
  }
}
