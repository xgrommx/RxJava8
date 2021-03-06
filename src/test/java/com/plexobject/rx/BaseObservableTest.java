package com.plexobject.rx;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plexobject.rx.scheduler.Scheduler;

public class BaseObservableTest {
    protected final List<String> names = Arrays.asList("Erica", "Matt", "John",
            "Mike", "Scott", "Alex", "Jeff", "Brad");

    protected CountDownLatch latch;
    protected AtomicReference<Throwable> onError;
    protected AtomicInteger onNext;
    protected AtomicInteger onCompleted;
    protected Scheduler[] allSchedulers = { Scheduler.newImmediateScheduler(),
            Scheduler.newNewThreadScheduler(),
            Scheduler.newTimerSchedulerWithMilliInterval(1) };

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
    }

    @Test
    public void testSubscribeManual() throws Exception {
        // Observable.from(names).subscribe(System.out::println,
        // Throwable::printStackTrace);
        //
        // Observable.from(names.stream()).subscribe(
        // name -> System.out.println(name),
        // error -> error.printStackTrace());
        //
        // Observable.from(names.iterator()).subscribe(
        // name -> System.out.println(name),
        // error -> error.printStackTrace());
        //
        // Observable.from(names.iterator()).subscribe(
        // name -> System.out.println(name),
        // error -> error.printStackTrace());

        // Observable.from(names.spliterator()).subscribe(
        // name -> System.out.println(name),
        // error -> error.printStackTrace());

        // Observable.just("value").subscribe(v -> System.out.println(v),
        // error -> error.printStackTrace());
        //
        // Observable.just(Arrays.asList(1, 2, 3)).subscribe(
        // num -> System.out.println(num),
        // error -> error.printStackTrace());
        //
        // Observable.create(observer -> {
        // for (String name : names) {
        // observer.onNext(name);
        // }
        // observer.onCompleted();
        // }).subscribe(System.out::println, Throwable::printStackTrace);
        //
        // Observable.range(4, 8).subscribe(num -> System.out.println(num),
        // error -> error.printStackTrace());
        // Observable.throwing(new Error("test error")).subscribe(
        // System.out::println, error -> System.err.println(error));
    }

    protected void initLatch(int maxLatch) {
        latch = new CountDownLatch(maxLatch);
        onError = new AtomicReference<Throwable>();
        onNext = new AtomicInteger();
        onCompleted = new AtomicInteger();
    }

    protected <T> Subscription setupCallback(Observable<T> observable,
            Consumer<T> onNextWork, boolean onComplete) throws Exception {
        if (onComplete) {
            return observable.subscribe(v -> {
                if (onNextWork != null) {
                    onNextWork.accept(v);
                }
                latch.countDown();
                onNext.incrementAndGet();
            }, error -> {
                latch.countDown();
                onError.set(error);
            }, () -> {
                latch.countDown();
                onCompleted.incrementAndGet();
            });
        } else {
            return observable.subscribe(v -> {
                if (onNextWork != null) {
                    onNextWork.accept(v);
                }
                latch.countDown();
                onNext.incrementAndGet();
            }, error -> {
                latch.countDown();
                onError.set(error);
            });
        }
    }
}
