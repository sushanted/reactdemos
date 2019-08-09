package sr.reactdemos.utils;

import java.time.Duration;
import java.util.function.Consumer;

import reactor.core.publisher.Mono;

public class Utils {

    public static void sleepForSeconds(final int seconds) {
	Mono.delay(Duration.ofSeconds(seconds)).block();
    }

    public static void print(final Object object) {
	System.out.println(object);
    }


    public static void printThreadName() {
	System.out.println("Ran on thread: " + Thread.currentThread().getName());
    }

    public static void printThreadName(final Object any) {
	printThreadName();
    }

    public static <T> T printThreadNameAny(final T any) {
	printThreadName();
	return any;
    }

    public static <T> Consumer<T> print(final String message) {
	return ob -> {
	    System.out.println(message);
	};
    }

    public static Runnable printRunnable(final String message) {
	return () -> {
	    System.out.println(message);
	};
    }

}
