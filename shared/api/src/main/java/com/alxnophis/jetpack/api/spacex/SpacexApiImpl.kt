package com.alxnophis.jetpack.api.spacex

import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy

class SpacexApiImpl(
    private val apolloClient: ApolloClient
) : SpacexApi {

    override suspend fun pastLaunches(): ApolloCall<LaunchesQuery.Data> =
        apolloClient
            .query(LaunchesQuery(sort = FIELD_LAUNCH_DATE_UTC, order = ORDER_DESC))
            .fetchPolicy(FetchPolicy.NetworkFirst)

    companion object {
        private const val FIELD_LAUNCH_DATE_UTC = "launch_date_utc"
        private const val ORDER_DESC = "desc"
    }
}
