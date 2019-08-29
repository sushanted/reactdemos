//Aug 13, 2019
package sr.reactdemos.mono.operators;

import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class MonoCompose {
    public static void main(final String[] args) {

	// The composition/transformation function
	// may choose to return an altogether new mono
	// may subscribe to passed mono an use the value to create a new mono
	// may just call operators on the passed mono and return the assembled

	// Basic difference between transform and compose : the function is called
	// for each subscriber of the resultant mono in compose while the function is
	// used to obtain the new mono and provide it to each subscriber in transform

	final AtomicInteger monoNumber = new AtomicInteger(0);

	System.out.println("Composing mono, ignoring original");

	Mono<String> composed = Mono.fromCallable(monoNumber::incrementAndGet)
		.doOnSubscribe(Utils.print("compose doesn't subscribe, but gives the mono directly to the function"))
		// compose uses defer inside
		// Create a defer mono and call f(mono)=>mono passing myself (I am a mono) as an
		// argument
		.compose(mono -> {
		    System.out.println("Composing mono : " + monoNumber.incrementAndGet());
		    return Mono.just(monoNumber.get()).map(String::valueOf);
		});

	System.out.println(//
		composed//
			.doOnSubscribe(Utils.print("subscribed to composed"))//
			.block()//
	);

	System.out.println(//
		composed//
			.doOnSubscribe(Utils.print("subscribed again to composed"))//
			.block()//
	);

	monoNumber.set(0);

	System.out.println("Composing mono, mapping original");

	composed = Mono.fromCallable(monoNumber::incrementAndGet)
		.doOnSubscribe(Utils.print("subscribed by compose's subscribers"))
		// compose uses defer inside
		// Create a defer mono and call f(mono)=>mono passing myself (I am a mono) as an
		// argument
		.compose(mono -> {
		    System.out.println("Composing mono : " + monoNumber.incrementAndGet());
		    return mono.map(String::valueOf);
		});

	System.out.println(//
		composed//
			.doOnSubscribe(Utils.print("subscribed to composed"))//
			.block()//
	);

	System.out.println(//
		composed//
			.doOnSubscribe(Utils.print("subscribed again to composed"))//
			.block()//
	);
	// A new mono

	monoNumber.set(0);

	System.out.println("Transforming mono, ignoring original");

	// Transform will be called while assembly
	Mono<String> transformed = Mono.fromCallable(monoNumber::incrementAndGet)
		.doOnSubscribe(Utils.print("transform doesn't subscribe, but gives the mono directly to the function"))
		// Call f(mono)=>mono passing myself as an argument
		.transform(mono -> {
		    System.out.println("Transforming mono, won't do again : " + monoNumber.incrementAndGet());
		    return Mono.just(monoNumber.get()).map(String::valueOf);
		});

	System.out.println(//
		transformed//
			.doOnSubscribe(Utils.print("subscribed to transformed"))//
			.block()//
	);

	System.out.println(//
		transformed//
			.doOnSubscribe(Utils.print("subscribed again to transformed"))//
			.block()//
	);

	monoNumber.set(0);

	System.out.println("Transforming mono, mapping original");

	// Transform will be called while assembly
	transformed = Mono.fromCallable(monoNumber::incrementAndGet)
		.doOnSubscribe(Utils.print("subscribed by transforms subscribers"))
		// Call f(mono)=>mono passing myself as an argument
		.transform(mono -> {
		    System.out.println("Transforming mono, won't do again");
		    // just map it and return
		    return mono.map(String::valueOf);
		});

	System.out.println(//
		transformed//
			.doOnSubscribe(Utils.print("subscribed to transformed"))//
			.block()//
	);

	System.out.println(//
		transformed//
			.doOnSubscribe(Utils.print("subscribed again to transformed"))//
			.block()//
	);
    }
}
