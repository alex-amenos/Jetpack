package com.alxnophis.jetpack.authentication.domain.usecase

import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class UseCaseAuthenticate {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> =
        coroutineScope {
            delay(3000L)
            when {
                hasAuthorization(email, password) -> success(Unit)
                else -> failure(AuthenticationError.WrongAuthentication)
            }
        }

    private fun hasAuthorization(email: String, password: String): Boolean =
        email == AUTHORIZED_EMAIL && password == AUTHORIZED_PASSWORD

    companion object {
        const val AUTHORIZED_EMAIL = "my@email.com"
        const val AUTHORIZED_PASSWORD = "12345678Aab"
    }
}
