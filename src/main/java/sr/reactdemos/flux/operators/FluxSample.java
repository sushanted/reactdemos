//Dec 31, 2019
package sr.reactdemos.flux.operators;

import java.time.Duration;

import reactor.core.publisher.Flux;
import sr.reactdemos.utils.Utils;

public class FluxSample {
    public static void main(final String[] args) {

	// demoSimpleSample();

	// demoSampleCompanion();

	// demoSampleFirst();

	// TODO : all next variants
    }

    private static void demoSampleCompanion() {
	Utils.demo(
		"Sample companion : last item in time window will be sampled, last flux element will always be sampled, discarding others");

	Flux.range(0, 31)//
		.delayElements(Duration.ofMillis(5))//
		// Skip sample in fifth interval
		.sample(Flux.interval(Duration.ofMillis(20)).filter(i -> i != 5))//
		.doOnDiscard(Object.class, Utils.printValue("Discarded"))//
		.doOnNext(Utils.printValue("Sampled"))//
		.blockLast();
    }

    private static void demoSimpleSample() {
	Utils.demo(
		"Simple sample : last item in time window will be sampled, last flux element will always be sampled, discarding others");

	Flux.range(0, 11)//
		.delayElements(Duration.ofMillis(5))//
		.sample(Duration.ofMillis(30))//
		.doOnDiscard(Object.class, Utils.printValue("Discarded"))//
		.doOnNext(Utils.printValue("Sampled"))//
		.blockLast();
    }

    private static void demoSampleFirst() {
	Utils.demo(
		"Sample first : first item in time window will be sampled, discarding others");

	Flux.range(0, 11)//
		.delayElements(Duration.ofMillis(5))//
		.sampleFirst(Duration.ofMillis(30))//
		.doOnDiscard(Object.class, Utils.printValue("Discarded"))//
		.doOnNext(Utils.printValue("Sampled"))//
		.blockLast();
    }
}
