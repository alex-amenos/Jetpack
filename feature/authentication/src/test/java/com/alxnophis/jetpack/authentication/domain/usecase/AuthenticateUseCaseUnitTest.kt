package com.alxnophis.jetpack.authentication.domain.usecase

import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase.Companion.AUTHORIZED_EMAIL
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase.Companion.AUTHORIZED_PASSWORD
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
private class AuthenticateUseCaseUnitTest : BaseUnitTest() {

    private val useCase by lazy {
        AuthenticateUseCase(testDispatcher)
    }

    @Test
    fun `WHEN email and password are valid THEN complete successfully`() {
        runTest {
            val result = useCase.invoke(VALID_EMAIL, VALID_PASSWORD)

            result shouldBe Unit.right()
        }
    }

    @Test
    fun `WHEN email or password are invalid THEN WrongAuthentication error`() {
        runTest {
            val result = useCase.invoke(INVALID_EMAIL, INVALID_PASSWORD)

            result shouldBe AuthenticationError.WrongAuthentication.left()
        }
    }

    companion object {
        private const val VALID_EMAIL = AUTHORIZED_EMAIL
        private const val VALID_PASSWORD = AUTHORIZED_PASSWORD
        private const val INVALID_EMAIL = "unittest@email.com"
        private const val INVALID_PASSWORD = "12345678"
    }
}
