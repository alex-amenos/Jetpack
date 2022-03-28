package com.alxnophis.jetpack.api.jsonplaceholder

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object JsonPlaceholderRetrofitFactory {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com"
    private const val TIMEOUT_CONNECT = 30L
    private const val TIMEOUT_READ = 30L
    private const val TIMEOUT_WRITE = 30L

    fun invoke(): JsonPlaceholderRetrofitService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(JsonPlaceholderRetrofitService::class.java)
    }

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            .build()
    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = when (BuildConfig.DEBUG) {
            true -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }
}
