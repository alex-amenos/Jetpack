package com.alxnophis.jetpack.api.spacex.model

sealed class SpacexApiError {
    object Parse : SpacexApiError()
    object Network : SpacexApiError()
    object Unknown : SpacexApiError()
    object Unexpected : SpacexApiError()
    data class Http(val statusCode: Int) : SpacexApiError()
}
