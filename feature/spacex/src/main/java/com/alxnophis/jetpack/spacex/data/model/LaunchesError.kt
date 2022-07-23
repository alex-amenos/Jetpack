package com.alxnophis.jetpack.spacex.data.model

internal sealed class LaunchesError {
    object Network : LaunchesError()
    object Parse : LaunchesError()
    object Unknown : LaunchesError()
    object Unexpected : LaunchesError()
    data class Http(val statusCode: Int) : LaunchesError()
}
