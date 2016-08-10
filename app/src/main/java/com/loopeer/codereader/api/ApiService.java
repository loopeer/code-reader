package com.loopeer.codereader.api;

import android.app.Application;

import com.loopeer.codereader.CodeReaderApplication;

import java.io.File;
import java.util.MissingResourceException;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    public static final String API_URL = "https://github.com/";

    private static ApiService sInstance;

    private Retrofit mRetrofit;

    public static synchronized ApiService getInstance() {
        if (sInstance == null) {
            sInstance = new ApiService();
        }
        return sInstance;
    }


    private OkHttpClient getClient() {
        return createOkHttpClient(CodeReaderApplication.getInstance());
    }

    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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