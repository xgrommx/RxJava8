package com.plexobject.rx;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.plexobject.rx.impl.ObservableDelegate;
import com.plexobject.rx.impl.ObservableImpl;
import com.plexobject.rx.impl.ObservableNever;
import com.plexobject.rx.impl.Observer;
import com.plexobject.rx.scheduler.Scheduler;
import com.plexobject.rx.util.NatsSpliterator;
import com.plexobject.rx.util.SpliteratorAdapter;
import com.plexobject.rx.util.Tuple;

/**
 * The Observable interface defines common methods of reactive extensions, which
 * are implemented in separate class. This is main interface that user interacts
 * with when creating Observable objects.
 * 
 * @author Shahzad Bhatti
 *
 * @param <T>
 *            type of subscription data
 */
public interface Observable<T> {
    /**
     * This method allows user to create Observable by passing a consumer
     * function for notifying subscribers.
     * 
     * @param consumer
     *            function that implements Consumer function
     * @return instance of Observable
     */
    public static <T> Observable<T> create(Consumer<Observer<T>> consumer) {
        Objects.requireNonNull(consumer);
        return new ObservableDelegate<T>(consumer);
    }

    /**
     * This method creates Observable from a array of objects
     * 
     * @param values
     *            - array of values
     * @return instance of Observable
     */
    @SafeVarargs
    public static <T> Observable<T> from(T... values) {
        return new ObservableImpl<T>(Stream.of(values), null);
    }

    /**
     * This method creates Observable from a given stream
     * 
     * @param stream
     *            - source input
     * @return instance of Observable
     */
    public static <T> Observable<T> from(Stream<T> stream) {
        Objects.requireNonNull(stream);
        return new ObservableImpl<T>(stream, null);
    }

    /**
     * This method creates Observable from an iterator
     * 
     * @param it
     *            - source iterator
     * @return instance of Observable
     */
    public static <T> Observable<T> from(Iterator<T> it) {
        Objects.requireNonNull(it);
        return new ObservableImpl<T>(new SpliteratorAdapter<T>(it).getStream(),
                null);
    }

    /**
     * This method creates Observable from a spliterator
     * 
     * @param it
     *            - source spliterator
     * @return instance of Observable
     */
    public static <T> Observable<T> from(Spliterator<T> it) {
        Objects.requireNonNull(it);
        return new ObservableImpl<T>(StreamSupport.stream(it, false), null);
    }

    /**
     * This method creates Obsevable from a collection
     * 
     * @param c
     *            - source collection
     * @return instance of Observable
     */
    public static <T> Observable<T> from(Collection<T> c) {
        Objects.requireNonNull(c);
        return new ObservableImpl<T>(c.stream(), null);
    }

    /**
     * This method creates an empty Observable, which will call onCompleted
     * after subscription
     * 
     * @return instance of Observable
     */
    public static <T> Observable<T> empty() {
        return Observable.from();
    }

    /**
     * This method creates an Observable with a single object. If that object is
     * a collection, then entire collection is treated as a single entity.
     * 
     * @param obj
     *            - source object
     * @return instance of Observable
     */
    public static <T> Observable<T> just(T obj) {
        Objects.requireNonNull(obj);
        return Observable.from(obj);
    }

    /**
     * This method creates an Observable that would call onError upon
     * subscription
     * 
     * @param error
     *            - source error
     * @return instance of Observable
     */
    public static <T> Observable<T> throwing(Throwable error) {
        Objects.requireNonNull(error);
        return new ObservableImpl<T>(Stream.<T> of(), error);
    }

    /**
     * Creates range of numbers starting at from until it reaches to exclusively
     * 
     * @param from
     *            - starting range of number
     * @param to
     *            - ending range of number exclusively
     * @return instance of Observable
     */
    public static Observable<Integer> range(int from, int to) {
        return new ObservableImpl<Integer>(IntStream.range(from, to).boxed(),
                null);
    }

    /**
     * Creates infinite natural numbers starting from given number
     * 
     * @param from
     *            - starting number
     * @return instance of Observable
     */
    public static Observable<Integer> integers(int from) {
        return new ObservableImpl<Integer>(
                new NatsSpliterator(from).getStream(), null);
    }

    /**
     * This method ignores subscription and doesn't call any method
     * 
     * @return instance of Observable
     */
    public static <T> Observable<T> never() {
        return new ObservableNever<T>();
    }

    /**
     * This method counts number of elements in stream and creates another
     * stream with that value that is consumed by the subscriber
     * 
     * @return
     */
    Observable<Long> count();

    /**
     * This method removes duplicates from internal stream
     * 
     * @return
     */
    Observable<T> distinct();

    /**
     * This method filters items in internal stream based on given predicate
     * 
     * @param predicate
     *            - predicate function
     * @return instance of Observable
     */
    Observable<T> filter(Predicate<? super T> predicate);

    /**
     * This method transforms internal streams using given mapper function
     * 
     * @param mapper
     *            - mapper function
     * @return instance of Observable
     */
    <R> Observable<R> map(Function<? super T, ? extends R> mapper);

    /**
     * This method transforms internal streams using given mapper function and
     * creates a single collection from multiple collections
     * 
     * @param mapper
     *            - mapper function
     * @return instance of Observable
     */
    <R> Observable<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper);

    /**
     * This method limits number of elments in internal stream that would be
     * pushed to the subscriber
     * 
     * @param maxSize
     *            - max size
     * @return instance of Observable
     */
    Observable<T> limit(long maxSize);

    /**
     * This method skips first n elements from internal stream
     * 
     * @param n
     *            - number of elements to skip
     * @return instance of Observable
     */
    Observable<T> skip(long n);

    /**
     * This method sorts internal straem
     * 
     * @return instance of Observable
     */
    Observable<T> sorted();

    /**
     * This method sorts internal stream using given comparator
     * 
     * @param comparator
     *            for sorting
     * @return instance of Observable
     */
    Observable<T> sorted(Comparator<? super T> comparator);

    /**
     * This method changes scheduler for this Observable
     * 
     * @param scheduler
     *            - to set
     * @return instance of Observable
     */
    Observable<T> subscribeOn(Scheduler scheduler);

    /**
     * This method merges internal stream with stream of another Observable
     * 
     * @param other
     *            - other Observable
     * @return instance of Observable
     */
    Observable<T> merge(Observable<? extends T> other);

    /**
     * This method zips current stream with another streams and creates new
     * Observable of type Tuple
     * 
     * @param other
     *            other stream
     * @return instance of Observable
     */
    <U> Observable<Tuple> zip(Observable<? extends U> other);

    /**
     * Converts internal stream into parallel stream (underlying stream must
     * support parallel processing)
     * 
     * @return instance of Observable that supports parallel stream
     */
    Observable<T> parallel();

    /**
     * This method subscribes given consumer and starts pushing the data. By
     * default, data is pushed asynchronously using thread-pool scheduler.
     * 
     * @param onNext
     *            - consumer function that is called to consume the data
     * @param onError
     *            - consumer function that is called for notifying an error
     * @param onCompletion
     *            - function that is invoked when all data is pushed
     *            successfully. This will not be called when an error occurs or
     *            when user unsubscribes using dispose method of subscription.
     * @return subscription handle that can be used for unsubscribing
     */
    Subscription subscribe(Consumer<T> onNext, Consumer<Throwable> onError,
            OnCompletion onCompletion);

    /**
     * This method subscribes given consumer and starts pushing the data. By
     * default, data is pushed asynchronously using thread-pool scheduler. Note:
     * This method doesn't take onCompletion function so user will not be
     * notified when all data is processed, which is not a problems for most
     * cases.
     * 
     * @param onNext
     *            - consumer function that is called to consume the data
     * @param onError
     *            - consumer function that is called for notifying an error
     * 
     * @return subscription handle that can be used for unsubscribing
     */
    Subscription subscribe(Consumer<T> onNext, Consumer<Throwable> onError);

    /**
     * This returns internal stream as a list
     * 
     * @return list of objects
     */
    List<T> toList();

    /**
     * This returns internal stream as a set
     * 
     * @return set of objects
     */
    Set<T> toSet();
}
