package subscribe;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

public class SampleSubscriber<T> extends BaseSubscriber<T> {

	public void hookOnSubscribe(Subscription subscription) {
		request(1);
		try {
			Thread.currentThread().sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Subscribed");
	}

	public void hookOnNext(T value) {
		request(1);
		System.out.println(value);

	}
}