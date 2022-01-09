package com.alxnophis.jetpack.authentication.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEffect
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
internal class AuthenticationViewModelUnitTest : BaseViewModelUnitTest() {

    companion object {
        private const val EMAIL = "your@email.com"
        private const val PASSWORD = "123456789Aab"
    }

    private val useCaseAuthenticateMock: UseCaseAuthenticate = mock()
    private val viewModel by lazy {
        AuthenticationViewModel(
            AuthenticationState(),
            useCaseAuthenticateMock
        )
    }

    @Test
    fun `WHEN start THEN validate initial state`() {
        runBlockingTest {
            viewModel.uiState.test {
                assertEquals(AuthenticationState(), awaitItem())
            }
        }
    }

    @Test
    fun `WHEN signIn state and ToggleAuthenticationMode event THEN validate state is signUp`() {
        runBlockingTest {
            viewModel.handleEvent(AuthenticationEvent.ToggleAuthenticationMode)

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(authenticationMode = AuthenticationMode.SIGN_UP),
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `WHEN signUp state and ToggleAuthenticationMode event THEN validate state is signIn`() {
        runBlockingTest {
            val viewModel = AuthenticationViewModel(
                AuthenticationState().copy(authenticationMode = AuthenticationMode.SIGN_UP),
                useCaseAuthenticateMock
            )

            viewModel.handleEvent(AuthenticationEvent.ToggleAuthenticationMode)

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(authenticationMode = AuthenticationMode.SIGN_IN),
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `WHEN error state and ErrorDismissed event THEN validate dismissed error`() {
        runBlockingTest {
            val viewModel = AuthenticationViewModel(
                AuthenticationState().copy(error = 1),
                useCaseAuthenticateMock
            )

            viewModel.handleEvent(AuthenticationEvent.ErrorDismissed)

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(error = null),
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `WHEN updated email and EmailChanged event THEN validate state change`() {
        runBlockingTest {
            viewModel.handleEvent(AuthenticationEvent.EmailChanged(EMAIL))

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(email = EMAIL),
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `WHEN updated password and PasswordChanged event THEN validate state change`() {
        runBlockingTest {
            viewModel.handleEvent(AuthenticationEvent.PasswordChanged(PASSWORD))

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(
                        password = PASSWORD,
                        passwordRequirements = listOf(
                            PasswordRequirements.EIGHT_CHARACTERS,
                            PasswordRequirements.CAPITAL_LETTER,
                            PasswordRequirements.NUMBER,
                        )
                    ),
                    awaitItem()
                )
            }
        }
    }

    /**
     * Using runBlockingTest test returns error:
     * This job has not completed yet: java.lang.IllegalStateException: This job has not completed yet
     *
     * Current Solution: Use runBlocking
     *
     * Issue: https://github.com/Kotlin/kotlinx.coroutines/issues/1204
     * Future solution: kotlin 1.6.0 using runTest (issues with Turbine 3th party)
     */
    @Test
    fun `WHEN Authenticate event and correct credentials on state THEN validate loading state sequence and navigate to next step`() {
        runBlocking {
            whenever(useCaseAuthenticateMock.invoke(EMAIL, PASSWORD)).thenReturn(Unit.right())
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD)
            val viewModel = AuthenticationViewModel(
                initialState,
                useCaseAuthenticateMock
            )

            viewModel.handleEvent(AuthenticationEvent.Authenticate)

            viewModel.uiState.test {
                assertEquals(
                    initialState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(isLoading = false),
                    awaitItem()
                )
            }
            viewModel.effect.test {
                assertEquals(
                    AuthenticationEffect.NavigateToNextStep,
                    awaitItem()
                )
            }
        }
    }

    /**
     * Using runBlockingTest test returns error:
     * This job has not completed yet: java.lang.IllegalStateException: This job has not completed yet
     *
     * Current Solution: Use runBlocking
     *
     * Issue: https://github.com/Kotlin/kotlinx.coroutines/issues/1204
     * Future solution: kotlin 1.6.0 using runTest (issues with Turbine 3th party)
     */
    @Disabled
    @Test
    fun `WHEN Authenticate event and incorrect credentials THEN validate loading and error state sequence`() {
        runBlocking {
            whenever(useCaseAuthenticateMock.invoke(EMAIL, PASSWORD)).thenReturn(AuthenticationError.WrongAuthentication.left())
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD)
            val viewModel = AuthenticationViewModel(
                initialState,
                useCaseAuthenticateMock
            )

            viewModel.handleEvent(AuthenticationEvent.Authenticate)

            viewModel.uiState.test {
                assertEquals(
                    initialState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(isLoading = false, error = R.string.authentication_auth_error),
                    awaitItem()
                )
            }
        }
    }
}
