# Deprecated

建议直接使用[Retrofit](https://github.com/square/retrofit)

# RxOkHttp

基于RxJava+OkHttp整合的Android异步Http封装库.

## Usage

- 导入依赖

```
compile 'com.shenghaiyang.rxokhttp:rxokhttp:1.0.0'
```

Gson Transformer:

```
compile 'com.shenghaiyang.rxokhttp:rxokhttp-gson:1.0.0'
```

在1.0.0版本中暂时只支持Gson的Transformer，计划在下个版本中添加对jackson、fastjson、moshi的支持。

- 在代码中使用：

```java
String url = "https:github.com";
OkHttpClient client = new OkHttpClient();
Observable<String> observable = new RxOkHttp.ObservableBuilder(client)
        .url(url)
        .get()
        .build()
        .compose(StringTransformer.create());
```

如果返回的是json，可以使用GsonTransformer：

```java
Observable<SomeEntity> observable = new RxOkHttp.ObservableBuilder(client)
        .url(url)
        .get()
        .build()
        .compose(GsonTransformer.create(SomeEntity.class));
```

注意：在使用transformer后，数据已经返回主线程，如果想获取下载流：

```
new RxOkHttp.ObservableBuilder(client)
        .url(url)
        .get()
        .build()
        .subscribe(new Subscriber<Response>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Response response) {
            	//response为OkHttp提供的Response，可通过response获取下载流。（ps：此处依旧在异步线程中）

            }
        });
```

## Contact me

如果在使用中有什么问题，请联系我[shenghaiyang@live.cn](mailto:shenghaiyang@live.cn)。

## License

RxJava:

```
Copyright 2013 Netflix, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

OkHttp3:

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
