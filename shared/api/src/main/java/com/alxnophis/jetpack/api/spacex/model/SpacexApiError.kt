package com.alxnophis.jetpack.api.spacex.model

sealed class SpacexApiError {
    data object Parse : SpacexApiError()
    data object Network : SpacexApiError()
    data object Unknown : SpacexApiError()
    data object Unexpected : SpacexApiError()
    data class Http(val statusCode: Int) : SpacexApiError()
}
