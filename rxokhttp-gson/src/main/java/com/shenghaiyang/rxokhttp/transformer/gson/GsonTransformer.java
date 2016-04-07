package com.shenghaiyang.rxokhttp.transformer.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

public class GsonTransformer {

    private GsonTransformer() {
    }

    public static <E> Observable.Transformer<Response, E> create(Class<E> eClass) {
        return create(new Gson(), eClass);
    }

    public static <E> Observable.Transformer<Response, E> create(final Gson gson, final Class<E> eClass) {
        return new Observable.Transformer<Response, E>() {
            @Override
            public Observable<E> call(Observable<Response> responseObservable) {
                return responseObservable.flatMap(new Func1<Response, Observable<E>>() {
                    @Override
                    public Observable<E> call(final Response response) {
                        Observable<E> observable = Observable.create(new Observable.OnSubscribe<E>() {
                            @Override
                            public void call(Subscriber<? super E> subscriber) {
                                try {
                                    String json = response.body().string();
                                    E entity = gson.fromJson(json, eClass);
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onNext(entity);
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

    public static <E> Observable.Transformer<Response, E> create(TypeToken<E> typeToken) {
        return create(new Gson(), typeToken);
    }

    public static <E> Observable.Transformer<Response, E> create(final Gson gson, final TypeToken<E> typeToken) {
        return new Observable.Transformer<Response, E>() {
            @Override
            public Observable<E> call(Observable<Response> responseObservable) {
                return responseObservable.flatMap(new Func1<Response, Observable<E>>() {
                    @Override
                    public Observable<E> call(final Response response) {
                        Observable<E> observable = Observable.create(new Observable.OnSubscribe<E>() {
                            @Override
                            public void call(Subscriber<? super E> subscriber) {
                                try {
                                    String json = response.body().string();
                                    E entity = gson.fromJson(json, typeToken.getType());
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onNext(entity);
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
