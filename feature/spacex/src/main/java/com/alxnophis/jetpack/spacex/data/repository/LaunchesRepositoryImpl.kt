package com.alxnophis.jetpack.spacex.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.api.spacex.SpacexApi
import com.alxnophis.jetpack.api.spacex.model.SpacexApiError
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel
import com.alxnophis.jetpack.spacex.data.model.mapper.mapTo
import kotlinx.coroutines.withContext

internal class LaunchesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val apiDataSource: SpacexApi
) : LaunchesRepository {

    override suspend fun getPastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<LaunchesError, List<PastLaunchDataModel>> =
        withContext(dispatcherProvider.io()) {
            apiDataSource
                .pastLaunches(hasToFetchDataFromNetworkOnly)
                .map { pastLaunches: LaunchesQuery.Data? -> pastLaunches?.mapTo() ?: emptyList() }
                .mapLeft { error: SpacexApiError ->
                    when (error) {
                        is SpacexApiError.Parse -> LaunchesError.Parse
                        is SpacexApiError.Http -> LaunchesError.Http(error.statusCode)
                        is SpacexApiError.Network -> LaunchesError.Network
                        is SpacexApiError.Unknown -> LaunchesError.Unknown
                        else -> LaunchesError.Unexpected
                    }
                }
        }
}
