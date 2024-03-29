package com.alxnophis.jetpack.api.spacex

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.right
import com.alxnophis.jetpack.api.spacex.model.SpacexApiError
import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.apollographql.apollo3.exception.ApolloParseException
import timber.log.Timber

class SpacexApiImpl(
    private val apolloClient: ApolloClient
) : SpacexApi {

    override suspend fun pastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<SpacexApiError, LaunchesQuery.Data?> =
        catch {
            val fetchPolicy = if (hasToFetchDataFromNetworkOnly) FetchPolicy.NetworkOnly else FetchPolicy.CacheFirst
            return apolloClient
                .query(LaunchesQuery(sort = FIELD_LAUNCH_DATE_UTC, order = ORDER_DESC))
                .fetchPolicy(fetchPolicy)
                .execute()
                .data
                .right()
        }.mapLeft { error: Throwable ->
            Timber.e("Error past launches. Exception: $error")
            when (error) {
                is ApolloParseException -> SpacexApiError.Parse
                is ApolloHttpException -> SpacexApiError.Http(error.statusCode)
                is ApolloNetworkException -> SpacexApiError.Network
                is ApolloException -> SpacexApiError.Unknown
                else -> SpacexApiError.Unexpected
            }
        }

    companion object {
        private const val FIELD_LAUNCH_DATE_UTC = "launch_date_utc"
        private const val ORDER_DESC = "desc"
    }
}
