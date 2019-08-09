//Aug 8, 2019
package sr.reactdemos.mono.operators;

import reactor.core.publisher.Mono;
import sr.reactdemos.utils.Utils;

public class MonoSwitchIfEmpty {
    public static void main(final String[] args) {
	System.out.println("\nSwitch if empty test:");
	Mono.empty()//
		.doOnSubscribe(Utils.print("\tSubscribed to original publisher"))
		.doOnTerminate(Utils.printRunnable("\tOriginal publisher completed")).switchIfEmpty(Mono.just("y"))//
		.doOnSubscribe(Utils.print("\tSubscribed to switched publisher"))
		.doOnNext(Utils.print("\tSwitched publisher emitted"))//
		.block();
    }
}
