package com.alxnophis.jetpack.spacex.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel

internal interface LaunchesRepository {
    suspend fun getPastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<LaunchesError, List<PastLaunchDataModel>>
}
