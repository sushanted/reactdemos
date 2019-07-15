package sr.reactdemos.mono.construct;

import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class MonoCreate {
  public static void main(final String[] args) {

    // Used to bridge legacy listeners/callbacks
    final Mono<String> mono = Mono.create(sink -> {
          // call the listener
      Utils.sleepForSeconds(2);
          // on receiving the response call success/failure on sink
          sink.success("completed");

          // sink.error(new RuntimeException());

    });

    mono.subscribe(System.out::println);

  }
}
