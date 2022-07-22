package com.alxnophis.jetpack.api.spacex

import com.alxnophis.jetpack.spacex.LaunchesQuery

interface SpacexApi {
    suspend fun pastLaunches(hasToFetchDataFromNetworkOnly: Boolean): LaunchesQuery.Data?
}
