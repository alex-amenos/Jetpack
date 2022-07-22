package com.alxnophis.jetpack.api.spacex

import arrow.core.Either
import com.alxnophis.jetpack.api.spacex.model.SpacexApiError
import com.alxnophis.jetpack.spacex.LaunchesQuery

interface SpacexApi {
    suspend fun pastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<SpacexApiError, LaunchesQuery.Data?>
}
