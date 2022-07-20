package com.alxnophis.jetpack.spacex.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.spacex.data.datasource.LaunchesDataSource
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel
import com.alxnophis.jetpack.spacex.data.model.LaunchesError

internal class LaunchesRepositoryImpl(
    private val launchesDataSource: LaunchesDataSource
) : LaunchesRepository {

    override suspend fun getPastLaunches(): Either<LaunchesError, List<PastLaunchesDataModel>> =
        launchesDataSource.getPastLaunches()
}
