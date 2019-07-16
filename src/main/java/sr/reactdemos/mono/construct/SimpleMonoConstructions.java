package sr.reactdemos.mono.construct;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SimpleMonoConstructions {

  public static void main(final String[] args) {

    // Simple subscription
    Mono.just("Hello world!")//
        .subscribe(System.out::println);

    final Mono<String> mono = Mono.just("Multiple subscriptions");

    // All below are different subscribers to the same mono
    System.out.println(mono.block());
    System.out.println(mono.block());
    mono.subscribe(System.out::println);
    mono.subscribe(System.out::println);
    mono.subscribe(System.out::println);

    // returns T so that can be assigned to any type of Mono
    final Mono<String> emptyMono = Mono.empty();
    // returns null as nothing is returned by the mono
    System.out.println("Empty mono value: " + emptyMono.block());

    // mono with error
    final Mono<Object> errorMono = Mono.error(() -> new RuntimeException("Error!"));
    try {
      errorMono.block();
    } catch (final RuntimeException e) {
      System.out.println(e.getMessage());
    }

    // Get a single mono and then cancel the subscription with the publisher
    System.out.println("First value:" + Mono.from(Flux.range(1, 4)).block());



  }

}
