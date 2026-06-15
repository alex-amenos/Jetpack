package com.alxnophis.jetpack.movies.domain.model

sealed interface MovieError {
    data object Network : MovieError
    data object NotFound : MovieError
    data object Unauthorized : MovieError
    data class Unknown(val message: String?) : MovieError
}
