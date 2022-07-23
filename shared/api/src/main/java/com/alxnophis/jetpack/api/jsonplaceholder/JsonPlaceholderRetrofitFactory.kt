package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.extensions.isDebugBuildType
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class JsonPlaceholderRetrofitFactory {
    private val okHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor())
            .build()

    operator fun invoke(): JsonPlaceholderRetrofitService =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(JsonPlaceholderRetrofitService::class.java)

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = when {
            isDebugBuildType() -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }
    }

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com"
        private const val TIMEOUT_CONNECT = 10L
        private const val TIMEOUT_READ = 10L
        private const val TIMEOUT_WRITE = 10L
    }
}
