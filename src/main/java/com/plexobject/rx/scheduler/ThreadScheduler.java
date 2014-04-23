package com.plexobject.rx.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.plexobject.rx.Disposable;
import com.plexobject.rx.impl.SubscriptionObserver;

public class ThreadScheduler implements Scheduler, Disposable {
	final ExecutorService defaulExecutor = Executors.newSingleThreadExecutor();
	private boolean shutdown;

	@Override
	public synchronized void dispose() {
		shutdown = true;
		defaulExecutor.shutdown();
	}

	@Override
	public <T> void scheduleTick(Consumer<SubscriptionObserver<T>> consumer,
	        SubscriptionObserver<T> subscription) {
		if (shutdown) {
			throw new IllegalStateException("Already shutdown");
		}
		defaulExecutor.submit(() -> {
			consumer.accept(subscription);
		});

	}
}
