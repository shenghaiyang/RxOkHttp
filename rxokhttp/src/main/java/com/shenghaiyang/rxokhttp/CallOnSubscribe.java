package com.shenghaiyang.rxokhttp;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

final class CallOnSubscribe implements Observable.OnSubscribe<Response> {

    private final Call call;

    public CallOnSubscribe(Call call) {
        this.call = call;
    }

    @Override
    public void call(Subscriber<? super Response> subscriber) {
        RequestArbiter requestArbiter = new RequestArbiter(call, subscriber);
        subscriber.add(requestArbiter);
        subscriber.setProducer(requestArbiter);
    }
}