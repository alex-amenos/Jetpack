package com.alxnophis.jetpack.authentication.domain.usecase

import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate.Companion.AUTHORIZED_EMAIL
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate.Companion.AUTHORIZED_PASSWORD
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class UseCaseAuthenticateUnitTest : BaseUnitTest() {

    private val usecase by lazy {
        UseCaseAuthenticate()
    }

    @Test
    fun `when email and password are valid then complete successfully`() {
        runTest {
            val result = usecase.invoke(VALID_EMAIL, VALID_PASSWORD)

            assertEquals(success(Unit), result)
        }
    }

    @Test
    fun `when email or password are invalid then WrongAuthentication error`() {
        runTest {
            val result = usecase.invoke(INVALID_EMAIL, INVALID_PASSWORD)

            assertEquals(failure<AuthenticationError>(AuthenticationError.WrongAuthentication), result)
        }
    }

    companion object {
        private const val VALID_EMAIL = AUTHORIZED_EMAIL
        private const val VALID_PASSWORD = AUTHORIZED_PASSWORD
        private const val INVALID_EMAIL = "unittest@email.com"
        private const val INVALID_PASSWORD = "12345678"
    }
}
