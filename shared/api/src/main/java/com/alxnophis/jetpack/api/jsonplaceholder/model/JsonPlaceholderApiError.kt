package com.alxnophis.jetpack.api.jsonplaceholder.model

sealed class JsonPlaceholderApiError {
    object Network : JsonPlaceholderApiError()
    object Unexpected : JsonPlaceholderApiError()
    data class Server(val statusCode: Int?) : JsonPlaceholderApiError()
    data class Unknown(val statusCode: Int?) : JsonPlaceholderApiError()
}
