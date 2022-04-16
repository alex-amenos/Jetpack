package com.alxnophis.jetpack.authentication.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

// TODO rename it to AuthenticateUseCase
class UseCaseAuthenticate {

    suspend fun invoke(
        email: String,
        password: String
    ): Either<AuthenticationError, Unit> = Either.catch(
        { AuthenticationError.WrongAuthentication },
        {
            coroutineScope {
                delay(3000L)
                when {
                    hasAuthorization(email, password) -> Unit
                    else -> throw AuthenticationError.WrongAuthentication
                }
            }
        }
    )

    private fun hasAuthorization(email: String, password: String): Boolean =
        email == AUTHORIZED_EMAIL && password == AUTHORIZED_PASSWORD

    companion object {
        const val AUTHORIZED_EMAIL = "my@email.com"
        const val AUTHORIZED_PASSWORD = "12345678Aab"
    }
}
