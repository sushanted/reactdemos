package sr.reactdemos.mono.construct;

import reactor.core.publisher.Mono;

public class MonoDefer {
  public static void main(String[] args) {
    
    // A hook can be injected to track the subscription
    // Mono can be changed on the fly
    Mono.defer(() -> {
          // hook to know when it was subscribed
      System.out.println("Subscribed!!");
      // This mono finally will be subscribed
      return Mono.just("hello");
    }).subscribe(System.out::println);
  }
}

