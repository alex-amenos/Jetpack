package com.alxnophis.jetpack.api.model

sealed class ApiError(
    val code: Int,
    val message: String,
    val type: String
)
