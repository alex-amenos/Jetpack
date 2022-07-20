package com.alxnophis.jetpack.spacex.data.model

sealed class LaunchesError {
    object Network : LaunchesError()
    object Parse : LaunchesError()
    object Unknown : LaunchesError()
}
