package com.alxnophis.jetpack.api.spacex

import com.alxnophis.jetpack.spacex.type.Date
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.adapter.DateAdapter
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig

/**
 * TODO - Add cache
 * Normalized cache: https://www.apollographql.com/docs/kotlin/v2/essentials/normalized-cache
 * Http cache: https://www.apollographql.com/docs/kotlin/v2/essentials/http-cache
 * Normalized cache example: https://github.com/BoD/android-graphql-sample/blob/master/app/src/main/kotlin/com/example/graphqlsample/api/apollo/ApolloModule.kt
 */
object SpacexApolloClientFactory {
    private const val SERVER_URL = "https://api.spacex.land/graphql/"
    private val okHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(loggingInterceptor())
        .build()

    operator fun invoke() = ApolloClient
        .Builder()
        .serverUrl(SERVER_URL)
        .okHttpClient(okHttpClient)
        .addCustomScalarAdapter(Date.type, DateAdapter)
        .build()

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = when (BuildConfig.DEBUG) {
            true -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }
}
