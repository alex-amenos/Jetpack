package com.alxnophis.jetpack.authentication.domain.usecase

import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase.Companion.AUTHORIZED_EMAIL
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase.Companion.AUTHORIZED_PASSWORD
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class AuthenticateUseCaseUnitTest : BaseUnitTest() {

    private val useCase by lazy {
        AuthenticateUseCase()
    }

    @Test
    fun `when email and password are valid then complete successfully`() {
        testScope.runTest {
            val result = useCase.invoke(VALID_EMAIL, VALID_PASSWORD)

            assertEquals(Unit.right(), result)
        }
    }

    @Test
    fun `when email or password are invalid then WrongAuthentication error`() {
        testScope.runTest {
            val result = useCase.invoke(INVALID_EMAIL, INVALID_PASSWORD)

            assertEquals(AuthenticationError.WrongAuthentication.left(), result)
        }
    }

    companion object {
        private const val VALID_EMAIL = AUTHORIZED_EMAIL
        private const val VALID_PASSWORD = AUTHORIZED_PASSWORD
        private const val INVALID_EMAIL = "unittest@email.com"
        private const val INVALID_PASSWORD = "12345678"
    }
}
