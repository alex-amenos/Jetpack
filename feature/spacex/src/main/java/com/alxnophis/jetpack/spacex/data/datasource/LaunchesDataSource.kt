package com.alxnophis.jetpack.spacex.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel

internal interface LaunchesDataSource {
    suspend fun getPastLaunches(): Either<LaunchesError, List<PastLaunchesDataModel>>
}
