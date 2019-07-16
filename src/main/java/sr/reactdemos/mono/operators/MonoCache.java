package sr.reactdemos.mono.operators;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class MonoCache {
  public static void main(final String[] args) {

    final Mono<Long> cachedMono = Mono.fromCallable(System::currentTimeMillis)//
        .cache(Duration.ofSeconds(2));

    cachedMono.subscribe(l -> System.out.println("Cached value:" + l));
    Utils.sleepForSeconds(1);
    cachedMono.subscribe(l -> System.out.println("Cached value:" + l));
    Utils.sleepForSeconds(2);
    // The cache will get expired at this time, next subscriber will get
    // new(non-cached) value
    cachedMono.subscribe(l -> System.out.println("New value:" + l));


    final AtomicBoolean throwError = new AtomicBoolean(false);

    final Mono<String> cachedErrorMono = Mono.fromCallable(() -> {
          // first time throw exception
      if (throwError.compareAndSet(false, true)) {
        throw new RuntimeException(String.valueOf(System.currentTimeMillis()));
      }
          // next time return success
      return "success";
        }).cache(Duration.ofSeconds(2));

    try {
      cachedErrorMono.block();
    } catch (final RuntimeException runtimeException) {
      System.out.println("Got exception this time" + runtimeException.getMessage());
    }

    Utils.sleepForSeconds(1);

    // This time we again get the cached exception
    try {
      cachedErrorMono.block();
    } catch (final RuntimeException runtimeException) {
      System.out.println("Got exception this time: " + runtimeException.getMessage());
    }

    Utils.sleepForSeconds(2);

    // Now we don't get exception
    System.out.println(cachedErrorMono.block());
  }
}
