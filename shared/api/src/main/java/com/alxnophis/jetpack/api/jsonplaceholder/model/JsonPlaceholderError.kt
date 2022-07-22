package com.alxnophis.jetpack.api.jsonplaceholder.model

sealed class JsonPlaceholderError {
    object Network: JsonPlaceholderError()
    object Unexpected: JsonPlaceholderError()
    data class Server(val statusCode: Int?): JsonPlaceholderError()
    data class Unknown(val statusCode: Int?): JsonPlaceholderError()
}
