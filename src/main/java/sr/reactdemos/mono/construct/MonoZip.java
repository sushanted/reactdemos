package sr.reactdemos.mono.construct;

import java.time.Duration;
import java.util.Date;

import reactor.core.publisher.Mono;

/**
 * Difference between 'when' and zip : 'when' always returns Mono<Void> , zip
 * returns Tuple 'when' accepts publishers, zip accepts only Monos
 *
 */
public class MonoZip {
  public static void main(final String[] args) {

    System.out.println("Starting first mono: " + new Date());

    // let all the monos complete, will take 3 minutes (delays will run in
    // parallel)
    System.out.println(//
        Mono.zip(//
            Mono.delay(Duration.ofSeconds(2)), //
            Mono.delay(Duration.ofSeconds(3)), //
            Mono.just("value")//
        )//
            .block()//
    );

    System.out.println("First mono completed: " + new Date());

    // Will get out on the first error immediately
    System.out.println(//
        Mono.zip(//
            Mono.error(RuntimeException::new), //
            Mono.delay(Duration.ofSeconds(2)), //
            Mono.delay(Duration.ofSeconds(3)//
            )//
        )//
            .onErrorResume(t -> Mono.empty())//
            .block()//
    );

    System.out.println("Error mono completed: " + new Date());

    // Error will be delayed, all the Publishers will complete
    System.out.println(//
        Mono.zipDelayError(//
            Mono.error(RuntimeException::new), //
            Mono.delay(Duration.ofSeconds(2)), //
            Mono.delay(Duration.ofSeconds(3)//
            )//
        )//
            .onErrorResume(t -> Mono.empty())//
            .block()//
    );

    System.out.println("Delay Error mono completed: " + new Date());

  }
}
