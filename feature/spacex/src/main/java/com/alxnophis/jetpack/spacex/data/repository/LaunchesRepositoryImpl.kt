package com.alxnophis.jetpack.spacex.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.data.datasource.LaunchesDataSource
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel
import com.alxnophis.jetpack.spacex.data.model.mapper.mapTo

internal class LaunchesRepositoryImpl(
    private val launchesDataSource: LaunchesDataSource
) : LaunchesRepository {

    override suspend fun getPastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<LaunchesError, List<PastLaunchDataModel>> =
        launchesDataSource
            .getPastLaunches(hasToFetchDataFromNetworkOnly)
            .map { pastLaunches: LaunchesQuery.Data? -> pastLaunches?.mapTo() ?: emptyList() }
}
