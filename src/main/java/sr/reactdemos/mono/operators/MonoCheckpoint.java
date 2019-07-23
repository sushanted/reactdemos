package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;

//TODO : not sure about the usage

public class MonoCheckpoint {

  public static void main(final String[] args) {
    Mono.just("hello")//
        // This will not be printed
        .checkpoint("first", true)//
        .map("x"::concat)//
        .map(x -> {
          if (!x.isEmpty()) {
            throw new RuntimeException();
          }
          return "";
        })//
        .filter(x -> x.isEmpty())//
        .map(x -> "")//
        // This will be printed
        .checkpoint("second", true)//
        .block();

  }
}
