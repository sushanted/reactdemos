//Jul 24, 2019
package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class MonoFilterWhen {
    public static void main(final String[] args) {
	System.out.println(Mono.just("x")//
		.filterWhen(x -> {
		    return Mono.just(true)//
			    .doOnSuccess(Utils::printThreadName);
		})//
		.block());

	System.out.println(Mono.just("x")//
		.filterWhen(x -> {
		    return Mono.just(false)//
			    .doOnSuccess(Utils::printThreadName);
		})//
		.block());

	System.out.println(Mono.just("x")//
		.filterWhen(x -> {
		    return Mono.<Boolean>empty()//
			    .doOnSuccess(Utils::printThreadName);
		})//
		.block());
    }
}
