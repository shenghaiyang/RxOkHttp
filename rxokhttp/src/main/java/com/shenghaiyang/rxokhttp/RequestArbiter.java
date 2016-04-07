package com.shenghaiyang.rxokhttp;

import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Response;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;

final class RequestArbiter extends AtomicBoolean implements Subscription, Producer {

    private final Call call;
    private final Subscriber<? super Response> subscriber;

    public RequestArbiter(Call call, Subscriber<? super Response> subscriber) {
        this.call = call;
        this.subscriber = subscriber;
    }

    @Override
    public void request(long n) {
        if (n < 0) throw new IllegalArgumentException("n < 0: " + n);
        if (n == 0) return;
        if (!compareAndSet(false, true)) return;
        try {
            Response response = call.execute();
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(response);
            }
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            if (!subscriber.isUnsubscribed()) {
                subscriber.onError(t);
            }
            return;
        }
        if (!subscriber.isUnsubscribed()) {
            subscriber.onCompleted();
        }
    }

    @Override
    public void unsubscribe() {
        call.cancel();
    }

    @Override
    public boolean isUnsubscribed() {
        return call.isCanceled();
    }
}
