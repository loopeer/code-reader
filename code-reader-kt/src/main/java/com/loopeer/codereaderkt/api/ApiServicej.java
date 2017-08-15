package com.loopeer.codereaderkt.api;

import android.app.Application;


import com.loopeer.codereaderkt.BuildConfig;
import com.loopeer.codereaderkt.CodeReaderApplication;

import java.io.File;
import java.util.MissingResourceException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServicej {

    public static final String API_URL = "https://api.github.com/";

    private static ApiServicej sInstance;

    private Retrofit mRetrofit;

    public static synchronized ApiServicej getInstance() {
        if (sInstance == null) {
            sInstance = new ApiServicej();
        }
        return sInstance;
    }


    private OkHttpClient getClient() {
        return createOkHttpClient(CodeReaderApplication.Companion.getInstance());
    }

    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpJsonLoggingInterceptor loggingInterceptor = new HttpJsonLoggingInterceptor();
            loggingInterceptor.setLevel(HttpJsonLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggingInterceptor);
        }

        httpClient.connectTimeout(1, TimeUnit.HOURS); // connect timeout
        httpClient.readTimeout(1, TimeUnit.HOURS);
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        httpClient.cache(cache);
        return httpClient.build();
    }

    protected Retrofit.Builder newRestAdapterBuilder() {
        return new Retrofit.Builder();
    }

    protected Retrofit getRetrofit() {
        if (mRetrofit == null) {
            try {
                mRetrofit = newRestAdapterBuilder()
                        .client(getClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .baseUrl(API_URL)
                        .build();
            } catch (NullPointerException e) {
                throw new MissingResourceException("Define your endpoint in api_url string resource.", getClass().getName(), "api_url");
            }
        }

        return mRetrofit;
    }

    public static <T> T create(Class<T> service) {
        return getInstance().getRetrofit().create(service);
    }

}