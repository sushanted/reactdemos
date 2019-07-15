package sr.reactdemos.mono.construct;

import java.time.Duration;

import reactor.core.publisher.Mono;

public class MonoFirst {
  public static void main(String[] args) {
     System.out.println("Finding fastest");
    
        System.out.println(
            Mono.first(//
                // Mono.just("got fastest"), //
                Mono.delay(Duration.ofSeconds(4)).map(d -> "got slow"), //
                Mono.delay(Duration.ofSeconds(2)).map(d -> "got fast")//
            ).block());
  }
}

