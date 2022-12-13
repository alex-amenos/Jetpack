package com.alxnophis.jetpack.api.spacex

import com.alxnophis.jetpack.api.BuildConfig
import com.alxnophis.jetpack.api.extensions.isDebugBuildType
import com.alxnophis.jetpack.spacex.type.Date
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.adapter.DateAdapter
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.okHttpClient
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class SpacexApolloClientFactory {
    private val memoryCache = MemoryCacheFactory(
        maxSizeBytes = MEMORY_MAX_SIZE_BYTES,
        expireAfterMillis = MEMORY_EXPIRATION_TIME_MILLIS
    )
    private val okHttpClient: OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor())
            .also { okHttpClientBuilder ->
                if (BuildConfig.DEBUG) {
                    okHttpClientBuilder.addInterceptor(OkHttpProfilerInterceptor())
                }
            }
            .build()

    operator fun invoke() =
        ApolloClient
            .Builder()
            .serverUrl(SERVER_URL)
            .normalizedCache(
                normalizedCacheFactory = memoryCache,
                writeToCacheAsynchronously = true
            )
            .okHttpClient(okHttpClient)
            .addCustomScalarAdapter(Date.type, DateAdapter)
            .build()

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = when {
            isDebugBuildType() -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }
    }

    companion object {
        private const val SERVER_URL = "https://api.spacex.land/graphql/"
        private const val TIMEOUT_CONNECT = 10L
        private const val TIMEOUT_READ = 10L
        private const val TIMEOUT_WRITE = 10L
        private const val MEMORY_MAX_SIZE_BYTES: Int = 10 * 1024 * 1024
        private const val MEMORY_EXPIRATION_TIME_MILLIS: Long = 5 * 60 * 1000
    }
}
