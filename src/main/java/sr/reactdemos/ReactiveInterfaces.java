package sr.reactdemos;

import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveInterfaces {

  public static void main(final String[] args) {

    final Publisher<Integer> customPublisher = subscriber -> {
      subscriber.onSubscribe(new CustomSubscription(subscriber));
    };

    final AtomicReference<Subscription> subscriptionReference = new AtomicReference<>();

    final Subscriber<Integer> customSubscriber = new Subscriber<Integer>() {

      @Override
      public void onSubscribe(final Subscription s) {
        System.out.println("Subscribed, requesting 1");
        s.request(1);
        subscriptionReference.set(s);
        // s.request(2);
        // s.request(1);
        // s.request(20); // This won't work as only 10 items can be published
      }

      @Override
      public void onNext(final Integer t) {
        System.out.println("Published: " + t);
        // System.out.println("Cancelling");
        // subscriptionReference.get().cancel();
        // This won't work as subscription is cancelled

        if (t < 9) {
          System.out.println("requesting: " + (t + 1));
          subscriptionReference.get().request(1);
          System.out.println("requested: " + (t + 1));
        }
        System.out.println("on next complete: " + t);
      }

      @Override
      public void onError(final Throwable t) {
        System.out.println("Error occured: " + t.getMessage());

      }

      @Override
      public void onComplete() {
        System.out.println("Completed");

      }
    };

    // customPublisher.subscribe(customSubscriber);

    Flux.range(0, 10)//
        .subscribe(customSubscriber);

	Mono.just(3)//
		.doOnTerminate(() -> System.out.println("on"))//
		.doAfterTerminate(() -> System.out.println("after"))//
		.subscribe(customSubscriber);

  }

  public static class CustomSubscription implements Subscription {

    private final Subscriber<? super Integer> subscriber;

    private int publishedCount = 0;

    public CustomSubscription(final Subscriber<? super Integer> subscriber) {
      this.subscriber = subscriber;
    }

    @Override
    public void request(final long n) {

      if (this.publishedCount >= 10) {
        return;
      }
      for (final int i = 0; i < n && this.publishedCount < 10; this.publishedCount++) {
        this.subscriber.onNext(this.publishedCount);
      }
      if (this.publishedCount >= 10) {
        this.subscriber.onComplete();
      }

    }

    @Override
    public void cancel() {

    }
  }
}
