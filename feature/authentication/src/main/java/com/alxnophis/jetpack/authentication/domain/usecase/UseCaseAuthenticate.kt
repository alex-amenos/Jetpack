package com.alxnophis.jetpack.authentication.domain.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import kotlinx.coroutines.delay

class UseCaseAuthenticate {

    companion object {
        const val AUTHORIZED_EMAIL = "my@email.com"
        const val AUTHORIZED_PASSWORD = "12345678Aab"
    }

    suspend operator fun invoke(
        email: String,
        password: String
    ): Either<AuthenticationError.WrongAuthentication, Unit> {
        delay(3000L)
        return when {
            email == AUTHORIZED_EMAIL && password == AUTHORIZED_PASSWORD -> Unit.right()
            else -> AuthenticationError.WrongAuthentication.left()
        }
    }
}
