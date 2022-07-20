package com.alxnophis.jetpack.spacex.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel
import com.alxnophis.jetpack.spacex.data.model.LaunchesError

internal interface LaunchesRepository {
    suspend fun getPastLaunches(): Either<LaunchesError, List<PastLaunchesDataModel>>
}
