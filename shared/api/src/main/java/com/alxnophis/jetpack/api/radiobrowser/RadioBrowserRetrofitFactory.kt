package com.alxnophis.jetpack.api.radiobrowser

import android.content.Context
import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import com.alxnophis.jetpack.api.interceptor.NetworkStatusInterceptor
import com.chuckerteam.chucker.api.ChuckerInterceptor
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class RadioBrowserRetrofitFactory(
    context: Context,
) {
    private val userAgentInterceptor =
        Interceptor { chain ->
            val request =
                chain
                    .request()
                    .newBuilder()
                    .addHeader("User-Agent", USER_AGENT)
                    .build()
            chain.proceed(request)
        }

    private val okHttpClient: OkHttpClient =
        OkHttpClient
            .Builder()
            .callTimeout(TIMEOUT_CALL, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            .addInterceptor(NetworkStatusInterceptor(context))
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
            .build()

    private val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .addConverterFactory(jsonConverter)
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    private companion object {
        const val BASE_URL = "https://de1.api.radio-browser.info/"
        const val USER_AGENT = "JetpackRadio/1.0"
        const val TIMEOUT_CALL = 15L
        const val TIMEOUT_CONNECT = 5L
        const val TIMEOUT_READ = 8L
        const val TIMEOUT_WRITE = 5L
        private val contentType = "application/json; charset=UTF-8".toMediaType()
        private val jsonConfiguration = Json { ignoreUnknownKeys = true }
        private val jsonConverter = jsonConfiguration.asConverterFactory(contentType)
    }
}
