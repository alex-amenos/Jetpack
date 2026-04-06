package com.alxnophis.jetpack.api.jsonplaceholder

import android.content.Context
import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import com.alxnophis.jetpack.api.BuildConfig
import com.alxnophis.jetpack.api.extensions.isDebugBuildType
import com.alxnophis.jetpack.api.interceptor.NetworkStatusInterceptor
import com.alxnophis.jetpack.api.interceptor.RetryInterceptor
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class JsonPlaceholderRetrofitFactory(
    context: Context,
) {
    private val cache: Cache = Cache(
        directory = File(context.cacheDir, HTTP_CACHE_DIR),
        maxSize = HTTP_CACHE_SIZE,
    )

    private val okHttpClient: OkHttpClient = OkHttpClient
        .Builder()
        .cache(cache)
        .callTimeout(TIMEOUT_CALL, TimeUnit.SECONDS)
        .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
        .addInterceptor(NetworkStatusInterceptor(context))
        .addInterceptor(retryInterceptor)
        .addInterceptor(loggingInterceptor())
        .addInterceptor(ChuckerInterceptor(context))
        .apply { certificatePinner(certificatePinner) }
        .also { okHttpClientBuilder ->
            if (BuildConfig.DEBUG) {
                okHttpClientBuilder.addInterceptor(OkHttpProfilerInterceptor())
            }
        }
        .build()

    operator fun invoke(): JsonPlaceholderRetrofitService = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .addConverterFactory(jsonConverter)
        .build()
        .create(JsonPlaceholderRetrofitService::class.java)

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = when {
            isDebugBuildType() -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }
    }

    private companion object {
        const val BASE_URL = "https://jsonplaceholder.typicode.com"
        const val BASE_URL_PATTERN = "jsonplaceholder.typicode.com"
        const val HTTP_CACHE_DIR = "http_cache"
        const val HTTP_CACHE_SIZE = 10L * 1024L * 1024L // 10 MB
        const val TIMEOUT_CALL = 15L
        const val TIMEOUT_CONNECT = 5L
        const val TIMEOUT_READ = 8L
        const val TIMEOUT_WRITE = 5L
        private val contentType = "application/json; charset=UTF8".toMediaType()
        private val jsonConfiguration = Json { ignoreUnknownKeys = true }
        private val jsonConverter = jsonConfiguration.asConverterFactory(contentType)
        private val retryInterceptor: RetryInterceptor = RetryInterceptor(
            maxRetries = 2,
            initialDelayMs = 500,
            maxDelayMs = 2000,
            backoffMultiplier = 1.5,
        )
        private val certificatePinner = CertificatePinner
            .Builder()
            .add(BASE_URL_PATTERN, "sha256/e89QAFJvkB7Tn3QGfsNheN8fgTxZgLECjap1xSq628w=")
            .add(BASE_URL_PATTERN, "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=")
            .add(BASE_URL_PATTERN, "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c=")
            .build()
    }
}
