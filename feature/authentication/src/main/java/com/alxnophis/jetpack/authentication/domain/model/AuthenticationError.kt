package com.alxnophis.jetpack.authentication.domain.model

sealed class AuthenticationError : Exception() {
    data object WrongAuthentication : AuthenticationError()
}
