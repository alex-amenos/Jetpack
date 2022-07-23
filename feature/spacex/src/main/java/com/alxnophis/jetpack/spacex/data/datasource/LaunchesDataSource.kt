package com.alxnophis.jetpack.spacex.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel

internal interface LaunchesDataSource {
    suspend fun getPastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<LaunchesError, List<PastLaunchDataModel>>
}
