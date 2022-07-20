package com.alxnophis.jetpack.api.spacex

import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient

class SpacexApiImpl(
    private val apolloClient: ApolloClient
) : SpacexApi {

    override suspend fun pastLaunches(): ApolloCall<LaunchesQuery.Data> =
        apolloClient.query(LaunchesQuery(sort = "launch_date_utc", order = "desc"))
}
