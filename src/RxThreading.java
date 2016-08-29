import rx.Observable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RxThreading
{
    public static final String MSG = "%d items counted by %d subscribers in %f second.%n";
    public static final int NUM_SUBSCRIBERS = 1;
    public static final int NUM_ITEMS = 10_000_000;

    private static int counter;

    public static void main(String[] args) throws Exception
    {
        long begin = System.nanoTime();
        CountDownLatch latch = new CountDownLatch(NUM_SUBSCRIBERS);

        Observable<?> observable =
            Observable.range(0, NUM_ITEMS / NUM_SUBSCRIBERS)
//                .flatMap(x -> Observable.timer(1, TimeUnit.SECONDS, Schedulers.immediate()))
                .flatMap(x -> Observable.timer(1, TimeUnit.SECONDS))
                .doOnNext(ignored -> counter++) // Callbacks are serialized... PER SUBSCRIBER!
                .doOnCompleted(latch::countDown);

        for (int i = 0; i < NUM_SUBSCRIBERS; i++) {
            observable.subscribe();
        }
        latch.await();

        System.out.printf(MSG, counter, NUM_SUBSCRIBERS, (System.nanoTime() - begin) / 1e9);
    }
}
