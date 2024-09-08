package com.alxnophis.jetpack.spacex.data.model

internal sealed class LaunchesError {
    data object Network : LaunchesError()
    data object Parse : LaunchesError()
    data object Unknown : LaunchesError()
    data object Unexpected : LaunchesError()
    data class Http(val statusCode: Int) : LaunchesError()
}
