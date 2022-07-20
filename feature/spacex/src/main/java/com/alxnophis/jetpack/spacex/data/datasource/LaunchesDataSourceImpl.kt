package com.alxnophis.jetpack.spacex.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.api.spacex.SpacexApi
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel
import com.alxnophis.jetpack.spacex.data.model.mapper.map
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.apollographql.apollo3.exception.ApolloParseException
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class LaunchesDataSourceImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val spacexApi: SpacexApi
) : LaunchesDataSource {

    override suspend fun getPastLaunches(): Either<LaunchesError, List<PastLaunchesDataModel>> =
        withContext(dispatcherProvider.io()) {
            Either.catch(
                { error: Throwable ->
                    Timber.e("Error getting past launches. Exception: $error")
                    when (error) {
                        is ApolloNetworkException -> LaunchesError.Network
                        is ApolloHttpException -> LaunchesError.Http(error.statusCode)
                        is ApolloParseException -> LaunchesError.Parse
                        else -> LaunchesError.Unknown
                    }
                },
                {
                    spacexApi
                        .pastLaunches()
                        .execute()
                        .data
                        ?.map()
                        ?: emptyList()
                }
            )
        }
}
