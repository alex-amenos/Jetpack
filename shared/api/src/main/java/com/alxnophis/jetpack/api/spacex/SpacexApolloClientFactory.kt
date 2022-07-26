package com.alxnophis.jetpack.api.spacex

import com.alxnophis.jetpack.api.extensions.isDebugBuildType
import com.alxnophis.jetpack.spacex.type.Date
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.adapter.DateAdapter
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.okHttpClient
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class SpacexApolloClientFactory() {
    private val memoryCache = MemoryCacheFactory(
        maxSizeBytes = 10 * 1024 * 1024,
        expireAfterMillis = 15 * 60 * 1000
    )
    private val memoryThenSqlCache: NormalizedCacheFactory = memoryCache
    private val okHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor())
            .build()

    operator fun invoke() =
        ApolloClient
            .Builder()
            .serverUrl(SERVER_URL)
            .okHttpClient(okHttpClient)
            .addCustomScalarAdapter(Date.type, DateAdapter)
            .normalizedCache(memoryThenSqlCache)
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
    }
}
