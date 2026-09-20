package com.alxnophis.jetpack.authentication.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

typealias Authenticated = Unit

internal class AuthenticateUseCase(
    private val delay: Long = DELAY,
) {
    context(dispatcher: CoroutineDispatcher)
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Either<AuthenticationError, Authenticated> =
        withContext(dispatcher) {
            either {
                delay(delay)
                ensure(hasAuthorization(email, password)) { AuthenticationError.WrongAuthentication }
                Authenticated
            }
        }

    private fun hasAuthorization(
        email: String,
        password: String,
    ): Boolean = email == AUTHORIZED_EMAIL && password == AUTHORIZED_PASSWORD

    companion object {
        const val AUTHORIZED_EMAIL = "my@email.com"
        const val AUTHORIZED_PASSWORD = "12345678Aab"
        private const val DELAY = 3000L
    }
}
