package com.alxnophis.jetpack.authentication.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

typealias Authorized = Unit

class AuthenticateUseCase(
    private val dispatchers: DispatcherProvider,
    private val delay: Long = DELAY,
) {

    suspend fun invoke(
        email: String,
        password: String
    ): Either<AuthenticationError, Authorized> = Either.catch(
        { AuthenticationError.WrongAuthentication },
        {
            withContext(dispatchers.io()) {
                delay(delay)
                if (hasAuthorization(email, password)) {
                    Authorized
                } else {
                    throw AuthenticationError.WrongAuthentication
                }
            }
        }
    )

    private fun hasAuthorization(email: String, password: String): Boolean =
        email == AUTHORIZED_EMAIL && password == AUTHORIZED_PASSWORD

    companion object {
        const val AUTHORIZED_EMAIL = "my@email.com"
        const val AUTHORIZED_PASSWORD = "12345678Aab"
        private const val DELAY = 3000L
    }
}
