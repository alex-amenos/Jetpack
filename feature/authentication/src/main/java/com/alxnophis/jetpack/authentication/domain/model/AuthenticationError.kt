package com.alxnophis.jetpack.authentication.domain.model

sealed class AuthenticationError : Exception() {
    object WrongAuthentication : AuthenticationError()
}
