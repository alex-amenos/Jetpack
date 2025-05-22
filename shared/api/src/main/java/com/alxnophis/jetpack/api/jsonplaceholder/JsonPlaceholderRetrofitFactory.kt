package com.alxnophis.jetpack.api.jsonplaceholder

import android.content.Context
import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import com.alxnophis.jetpack.api.BuildConfig
import com.alxnophis.jetpack.api.extensions.isDebugBuildType
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class JsonPlaceholderRetrofitFactory(
    context: Context,
) {
    private val okHttpClient: OkHttpClient =
        OkHttpClient
            .Builder()
            .callTimeout(TIMEOUT_CALL, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor())
            .addInterceptor(ChuckerInterceptor(context))
            .also { okHttpClientBuilder ->
                if (BuildConfig.DEBUG) {
                    okHttpClientBuilder.addInterceptor(OkHttpProfilerInterceptor())
                }
            }.build()

    operator fun invoke(): JsonPlaceholderRetrofitService =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .addConverterFactory(jsonConverter)
            .build()
            .create(JsonPlaceholderRetrofitService::class.java)

    private fun loggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level =
                when {
                    isDebugBuildType() -> HttpLoggingInterceptor.Level.BODY
                    else -> HttpLoggingInterceptor.Level.NONE
                }
        }

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com"
        private const val TIMEOUT_CALL = 15L
        private const val TIMEOUT_CONNECT = 10L
        private const val TIMEOUT_READ = 10L
        private const val TIMEOUT_WRITE = 10L
        private val contentType = "application/json; charset=UTF8".toMediaType()
        private val jsonConfiguration = Json { ignoreUnknownKeys = true }
        private val jsonConverter = jsonConfiguration.asConverterFactory(contentType)
    }
}
