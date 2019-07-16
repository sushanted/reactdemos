package sr.reactdemos.mono.construct;

import java.time.Duration;
import java.util.Date;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoWhen {
  public static void main(final String[] args) {

    System.out.println("Starting first mono: " + new Date());

    // let all the publishers complete, will take 3 minutes (delays will run in
    // parallel)
    Mono.when(//
        Flux.range(0, 5),
        Mono.delay(Duration.ofSeconds(2)), //
        Mono.delay(Duration.ofSeconds(3))//
    ).block();

    System.out.println("First mono completed: " + new Date());

    // Will get out on the first error immediately
    Mono.when(//
        Mono.error(RuntimeException::new), //
        Mono.delay(Duration.ofSeconds(2)), //
        Mono.delay(Duration.ofSeconds(3))//
    ).onErrorResume(t -> Mono.empty())
        .block();

    System.out.println("Error mono completed: " + new Date());

    // Error will be delayed, all the Publishers will complete
    Mono.whenDelayError(//
        Mono.error(RuntimeException::new), //
        Mono.delay(Duration.ofSeconds(2)), //
        Mono.delay(Duration.ofSeconds(3))//
    ).onErrorResume(t -> Mono.empty()).block();

    System.out.println("Delay Error mono completed: " + new Date());

  }
}
