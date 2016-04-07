package com.shenghaiyang.rxokhttp;

import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

public class StringTransformer {

    private StringTransformer() {
    }

    public static Observable.Transformer<Response, String> create() {
        return new Observable.Transformer<Response, String>() {
            @Override
            public Observable<String> call(final Observable<Response> responseObservable) {
                return responseObservable.flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(final Response response) {
                        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                try {
                                    String result = response.body().string();
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onNext(result);
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
                        });
                        return observable.observeOn(AndroidSchedulers.mainThread());
                    }
                });
            }
        };
    }
}
