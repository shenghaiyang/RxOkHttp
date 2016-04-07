package com.shenghaiyang.rxokhttp;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RxOkHttp {

    private RxOkHttp() {
    }

    public static class ObservableBuilder {
        private OkHttpClient client;
        private HttpUrl url;
        private String method;
        private Headers.Builder headers;
        private RequestBody body;
        private Object tag;

        public ObservableBuilder(OkHttpClient client) {
            if (client == null) {
                throw new NullPointerException("OkHttpClient can not be a null value.");
            }
            this.client = client;
        }

        public ObservableBuilder url(String url) {
            if (url == null) throw new IllegalArgumentException("url == null");
            // Silently replace websocket URLs with HTTP URLs.
            if (url.regionMatches(true, 0, "ws:", 0, 3)) {
                url = "http:" + url.substring(3);
            } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
                url = "https:" + url.substring(4);
            }
            HttpUrl parsed = HttpUrl.parse(url);
            if (parsed == null) throw new IllegalArgumentException("unexpected url: " + url);
            this.url = parsed;
            return this;
        }

        public ObservableBuilder header(String name, String value) {
            headers.set(name, value);
            return this;
        }

        public ObservableBuilder addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public ObservableBuilder removeHeader(String name) {
            headers.removeAll(name);
            return this;
        }

        public ObservableBuilder headers(Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        public ObservableBuilder cacheControl(CacheControl cacheControl) {
            String value = cacheControl.toString();
            if (value.isEmpty()) return removeHeader("Cache-Control");
            return header("Cache-Control", value);
        }

        public ObservableBuilder get() {
            return method("GET", null);
        }

        public ObservableBuilder head() {
            return method("HEAD", null);
        }

        public ObservableBuilder post(RequestBody body) {
            return method("POST", body);
        }

        public ObservableBuilder delete(RequestBody body) {
            return method("DELETE", body);
        }

        public ObservableBuilder delete() {
            return delete(RequestBody.create(null, new byte[0]));
        }

        public ObservableBuilder put(RequestBody body) {
            return method("PUT", body);
        }

        public ObservableBuilder patch(RequestBody body) {
            return method("PATCH", body);
        }

        public ObservableBuilder method(String method, RequestBody body) {
            if (method == null || method.length() == 0) {
                throw new IllegalArgumentException("method == null || method.length() == 0");
            }
            if (body != null && !HttpMethod.permitsRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must not have a request body.");
            }
            if (body == null && HttpMethod.requiresRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must have a request body.");
            }
            this.method = method;
            this.body = body;
            return this;
        }

        public ObservableBuilder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Observable<Response> build() {
            if (url == null) throw new IllegalStateException("url == null");
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            if (headers != null) {
                builder.headers(headers.build());
            }
            builder.tag(tag);
            builder.method(method, body);
            Request request = builder.build();
            return asyncRequest(client, request);
        }

    }

    public static Observable<Response> asyncRequest(OkHttpClient client, Request request) {
        Call call = client.newCall(request);
        return Observable.create(new CallOnSubscribe(call)).subscribeOn(Schedulers.io());
    }
}
