package com.alxnophis.jetpack.authentication.domain.model

sealed class AuthenticationError : Throwable() {
    object WrongAuthentication : AuthenticationError()
}
