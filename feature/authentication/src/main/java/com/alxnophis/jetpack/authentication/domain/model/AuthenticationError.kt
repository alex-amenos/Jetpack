package com.alxnophis.jetpack.authentication.domain.model

sealed class AuthenticationError {
    object WrongAuthentication: AuthenticationError()
    object GenericError: AuthenticationError()
}
