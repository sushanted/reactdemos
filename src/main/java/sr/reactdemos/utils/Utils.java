package sr.reactdemos.utils;

import java.io.IOException;
import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import reactor.core.publisher.Mono;

public class Utils {

    public static void sleepForSeconds(final int seconds) {
	Mono.delay(Duration.ofSeconds(seconds)).block();
    }

    public static void sleepForMillis(final int millis) {
	Mono.delay(Duration.ofMillis(millis)).block();
    }

    public static void sleepForMinutes(final int minutes) {
	Mono.delay(Duration.ofMinutes(minutes)).block();
    }

    public static void nonReactiveSleepForMillis(final int millis) {
	try {
	    Thread.sleep(millis);
	} catch (final InterruptedException e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
    }

    public static void print(final Object object) {
	System.out.println(object);
    }

    public static Consumer<Object> printThreadNameWithMsg(final String message) {
	return any -> System.out.println(message + " : " + Thread.currentThread().getName());
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

    public static <T> Consumer<T> printValue(final String message) {
	return ob -> {
	    System.out.println(message + " : " + ob);
	};
    }

    public static <T, U> BiConsumer<T, U> printExceptionAndValue(final String message) {
	return (t, o) -> {
	    System.out.println(message + " Error " + t + " for " + o);
	};
    }

    public static <T, U> BiConsumer<T, U> printExceptionAndValue() {
	return (t, o) -> {
	    System.out.println("Error " + t + " for " + o);
	};
    }

    public static void printExceptionAndValue(final Object t, final Object o) {
	System.out.println("Error " + t + " for " + o);
    }

    public static LongConsumer printLongWithMsg(final String message) {
	return ob -> {
	    System.out.println(message + " : " + ob);
	};
    }

    public static LongConsumer printConsumingLong(final String message) {
	return l -> {
	    System.out.println(message);
	};
    }

    public static Runnable printRunnable(final String message) {
	return () -> {
	    System.out.println(message);
	};
    }

    public static void waitForUserInput() {
	System.out.println("Hit enter key to continue...");
	try {
	    System.in.read();
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    public static void demo(final String demoCase) {
	System.out.println("\n\n" + demoCase);
    }

    public static class TimeTracker {

	private long startTime = System.currentTimeMillis();

	public TimeTracker resetTime() {
	    this.startTime = System.currentTimeMillis();
	    return this;
	}

	public <T> Consumer<T> printValue(final String message) {
	    return ob -> {
		System.out.println(
			message + " received " + ob + " at " + (System.currentTimeMillis() - this.startTime));
	    };
	}
    }

}
