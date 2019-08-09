package sr.reactdemos.mono.construct;

import java.time.Duration;

import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class MonoDelay {
    public static void main(final String[] args) {

	Mono.delay(Duration.ofSeconds(1))//
		// Always prints 0, runs all downstream on parallel scheduler
		.map(Utils::printThreadNameAny)//
		.subscribe(Utils::printThreadName);

	// Blocks the current thread till the mono returns, even if it is running on
	// different scheduler
	Mono.delay(Duration.ofSeconds(3)).block();
    }
}
