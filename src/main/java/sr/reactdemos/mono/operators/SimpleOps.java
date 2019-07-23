package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;

public class SimpleOps {
  public static void main(final String[] args) {

    // defaultIfEmpty
    System.out.println(Mono.justOrEmpty(null).defaultIfEmpty("abc").block());


  }
}
