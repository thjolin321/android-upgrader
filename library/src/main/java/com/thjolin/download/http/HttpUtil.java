package com.thjolin.download.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tanghao on 2021/5/27
 */
public class HttpUtil {

    private OkHttpClient okHttpClient;

    private HttpUtil() {
    }

    private static class SingletonHolder {
        private static final HttpUtil INSTANCE = new HttpUtil();
    }

    public static HttpUtil with() {
        return SingletonHolder.INSTANCE;
    }

    public static HttpUtil with(OkHttpClient okHttpCliaent) {
        SingletonHolder.INSTANCE.okHttpClient = okHttpCliaent;
        return SingletonHolder.INSTANCE;
    }

    public Response asyncCall(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return getOkHttpClient().newCall(request).execute();
    }

    public Response syncResponse(String url, long start, long end) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                //Range 请求头格式Range: bytes=start-end
                .addHeader("Range", "bytes=" + start + "-" + end)
                .build();
        return getOkHttpClient().newCall(request).execute();
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS).build();
        }
        return okHttpClient;
    }

}