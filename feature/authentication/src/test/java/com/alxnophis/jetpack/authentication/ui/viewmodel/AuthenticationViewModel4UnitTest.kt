package com.alxnophis.jetpack.authentication.ui.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEffect
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.testing.base.BaseViewModel4UnitTest
import com.alxnophis.jetpack.testing.extensions.testFix
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
internal class AuthenticationViewModel4UnitTest : BaseViewModel4UnitTest() {

    companion object {
        private const val EMAIL = "my@email.com"
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
        runTest {
            viewModel.uiState.test {
                assertEquals(AuthenticationState(), awaitItem())
            }
        }
    }

    @Test
    fun `WHEN signIn state and ToggleAuthenticationMode event THEN validate state is signUp`() {
        runTest {
            viewModel.setEvent(AuthenticationEvent.ToggleAuthenticationMode)

            advanceUntilIdle()

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
        runTest {
            val viewModel = AuthenticationViewModel(
                AuthenticationState().copy(authenticationMode = AuthenticationMode.SIGN_UP),
                useCaseAuthenticateMock
            )

            viewModel.setEvent(AuthenticationEvent.ToggleAuthenticationMode)

            advanceUntilIdle()

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
        runTest {
            val viewModel = AuthenticationViewModel(
                AuthenticationState().copy(error = 1),
                useCaseAuthenticateMock
            )

            viewModel.setEvent(AuthenticationEvent.ErrorDismissed)

            advanceUntilIdle()

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
        runTest {
            viewModel.setEvent(AuthenticationEvent.EmailChanged(EMAIL))

            advanceUntilIdle()

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
        runTest {
            viewModel.setEvent(AuthenticationEvent.PasswordChanged(PASSWORD))

            advanceUntilIdle()

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

    @Test
    fun `WHEN Authenticate event and correct credentials on state THEN validate loading state sequence and navigate to next step`() {
        runTest {
            whenever(useCaseAuthenticateMock.invoke(any(), any())).thenReturn(success(Unit))
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = AuthenticationViewModel(
                initialState,
                useCaseAuthenticateMock
            )

            viewModel.setEvent(AuthenticationEvent.Authenticate)

            advanceUntilIdle()

            viewModel.uiState.testFix {
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

    @Test
    fun `WHEN Authenticate event and incorrect credentials THEN validate loading and error state sequence`() {
        runTest {
            whenever(useCaseAuthenticateMock.invoke(any(), any())).thenReturn(failure(AuthenticationError.WrongAuthentication))
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, error = null)
            val viewModel = AuthenticationViewModel(
                initialState,
                useCaseAuthenticateMock
            )

            viewModel.setEvent(AuthenticationEvent.Authenticate)

            advanceUntilIdle()

            viewModel.uiState.test {
                assertEquals(
                    initialState.copy(isLoading = true, error = null),
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(isLoading = false, error = R.string.authentication_auth_error),
                    awaitItem()
                )
            }
            viewModel.effect.test {
                expectNoEvents()
            }
        }
    }
}
