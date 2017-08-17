package com.loopeer.codereaderkt.api

import android.app.Application
import com.loopeer.codereaderkt.BuildConfig
import com.loopeer.codereaderkt.CodeReaderApplications
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


open class ApiService {

    private var mRetrofit: Retrofit? = null


    private val client: OkHttpClient
        get() = CodeReaderApplications().getInstance()?.let { createOkHttpClient(it) }!!

    val retrofit: Retrofit
        get() {
            if (mRetrofit == null) {
//                try {
                    mRetrofit = Retrofit.Builder()
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .baseUrl(API_URL)
                            .build()
                /*} catch (e: NullPointerException) {
                    throw MissingResourceException("Define your endpoint in api_url string resource.", javaClass.name, "api_url")
                }*/

            }

            return mRetrofit!!
        }

    companion object {

        val API_URL = "https://api.github.com/"

        private var sInstance: ApiService? = null

        val instance: ApiService
            @Synchronized get() {
                if (sInstance == null) {
                    sInstance = ApiService()
                }
                return sInstance!!
            }

        internal val DISK_CACHE_SIZE = 50 * 1024 * 1024 // 50MB

        internal fun createOkHttpClient(app: Application): OkHttpClient {
            val httpClient = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpJsonLoggingInterceptor()
                loggingInterceptor.level = HttpJsonLoggingInterceptor.Level.BODY
                httpClient.addInterceptor(loggingInterceptor)
            }

            httpClient.connectTimeout(1, TimeUnit.HOURS) // connect timeout
            httpClient.readTimeout(1, TimeUnit.HOURS)
            val cacheDir = File(app.cacheDir, "http")
            val cache = Cache(cacheDir, DISK_CACHE_SIZE.toLong())
            httpClient.cache(cache)
            return httpClient.build()
        }

        fun <T> create(service: Class<T>): T {
            return instance.retrofit.create(service)
        }
    }

}