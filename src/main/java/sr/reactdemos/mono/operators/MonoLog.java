//Jul 24, 2019
package sr.reactdemos.mono.operators;

import java.util.logging.Level;

import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

public class MonoLog {
    public static void main(final String[] args) {

	System.out.println("Simple logs");

	Mono.just("x").log().block();

	System.out.println("Error logs");

	// log is for a single subscription instance, follows logs for multiple
	// instances
	System.out.println(Mono.error(RuntimeException::new).log().onErrorReturn(23).log().block());

	System.out.println("Verbose logging options");
	// onNext won't be displyed
	Mono.just("x")//
		.log("org.spring", Level.WARNING, SignalType.ON_COMPLETE, SignalType.ON_SUBSCRIBE)//
		.block();

	System.out.println("Verbose logging options : operator line");
	Mono.just("x")//
		.log(//
			"org.spring", //
			Level.WARNING, //
			true, //
			SignalType.SUBSCRIBE, //
			SignalType.REQUEST, //
			SignalType.ON_COMPLETE, //
			SignalType.ON_SUBSCRIBE, //
			SignalType.ON_NEXT//
		)//
		.log()//
		.block();
    }
}
