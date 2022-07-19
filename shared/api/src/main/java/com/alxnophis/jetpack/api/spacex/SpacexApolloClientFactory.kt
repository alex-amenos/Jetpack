package com.alxnophis.jetpack.api.spacex

import com.apollographql.apollo3.ApolloClient

object SpacexApolloClientFactory {
    private const val SERVER_URL = "https://api.spacex.land/graphql/"

    operator fun invoke() = ApolloClient
        .Builder()
        .serverUrl(SERVER_URL)
        .build()
}
