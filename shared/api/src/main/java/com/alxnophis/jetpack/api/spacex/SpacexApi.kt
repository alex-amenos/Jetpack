package com.alxnophis.jetpack.api.spacex

import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.apollographql.apollo3.ApolloCall

interface SpacexApi {
    suspend fun pastLaunches(): ApolloCall<LaunchesQuery.Data>
}
