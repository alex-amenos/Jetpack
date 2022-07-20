package com.alxnophis.jetpack.api.spacex

import android.content.Context
import com.alxnophis.jetpack.spacex.type.Date
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.adapter.DateAdapter
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig

class SpacexApolloClientFactory(applicationContext: Context) {
    private val sqlCache = SqlNormalizedCacheFactory(applicationContext, "spacex.db")
    private val memoryCache = MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024)
    private val memoryThenSqlCache = memoryCache.chain(sqlCache)
    private val okHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(loggingInterceptor())
        .build()

    operator fun invoke() = ApolloClient
        .Builder()
        .serverUrl(SERVER_URL)
        .okHttpClient(okHttpClient)
        .addCustomScalarAdapter(Date.type, DateAdapter)
        .normalizedCache(memoryThenSqlCache)
        .build()

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = when (BuildConfig.DEBUG) {
            true -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    companion object {
        private const val SERVER_URL = "https://api.spacex.land/graphql/"
    }
}
