package sr.reactdemos.mono.construct;

import java.time.Duration;

import reactor.core.publisher.Mono;

public class MonoDelay {
  public static void main(final String[] args) {

     Mono.delay(Duration.ofSeconds(1))//
        // Always prints 0, runs all downstream on parallel scheduler
            .map(l -> {
              System.out.println("Ran on thread: " + Thread.currentThread().getName());
              return String.valueOf(l);
            })//
            .subscribe(l -> {
              System.out.println("Ran on thread: " + Thread.currentThread().getName());
            });

        // Blocks the current thread till the mono returns, even if it is running on
        // different scheduler
        Mono.delay(Duration.ofSeconds(3)).block();
  }
}

