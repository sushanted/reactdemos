package sr.reactdemos.utils;

import java.time.Duration;

import reactor.core.publisher.Mono;

public class Utils {

  public static void sleepForSeconds(final int seconds) {
    Mono.delay(Duration.ofSeconds(seconds)).block();
  }

  public static void print(final Object object) {
    System.out.println(object);
  }

}
