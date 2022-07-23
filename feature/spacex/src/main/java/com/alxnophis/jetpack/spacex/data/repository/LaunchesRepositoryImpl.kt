package com.alxnophis.jetpack.spacex.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.spacex.data.datasource.LaunchesDataSource
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel

internal class LaunchesRepositoryImpl(
    private val launchesDataSource: LaunchesDataSource
) : LaunchesRepository {

    override suspend fun getPastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<LaunchesError, List<PastLaunchDataModel>> =
        launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)
}
