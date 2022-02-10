package com.alxnophis.jetpack.authentication.ui.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationViewAction
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.testing.base.BaseViewModel5UnitTest
import com.alxnophis.jetpack.testing.extensions.testFix
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
internal class AuthenticationViewModelUnitTest : BaseViewModel5UnitTest() {

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
                assertEquals(
                    AuthenticationState(),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN signIn state and ToggleAuthenticationMode event THEN validate state is signUp`() {
        runTest {
            viewModel.setAction(AuthenticationViewAction.ToggleAuthenticationMode)

            advanceUntilIdle()

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(authenticationMode = AuthenticationMode.SIGN_UP),
                    awaitItem()
                )
                expectNoEvents()
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

            viewModel.setAction(AuthenticationViewAction.ToggleAuthenticationMode)

            advanceUntilIdle()

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(authenticationMode = AuthenticationMode.SIGN_IN),
                    awaitItem()
                )
                expectNoEvents()
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

            viewModel.setAction(AuthenticationViewAction.ErrorDismissed)

            advanceUntilIdle()

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(error = null),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN updated email and EmailChanged event THEN validate state change`() {
        runTest {
            viewModel.setAction(AuthenticationViewAction.EmailChanged(EMAIL))

            advanceUntilIdle()

            viewModel.uiState.test {
                assertEquals(
                    AuthenticationState().copy(email = EMAIL),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN updated password and PasswordChanged event THEN validate state change`() {
        runTest {
            viewModel.setAction(AuthenticationViewAction.PasswordChanged(PASSWORD))

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
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN Authenticate event started THEN show loading state`() {
        runTest {
            whenever(useCaseAuthenticateMock.invoke(any(), any())).thenReturn(success(Unit))
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = AuthenticationViewModel(
                initialState,
                useCaseAuthenticateMock
            )

            viewModel.setAction(AuthenticationViewAction.Authenticate)

            advanceUntilIdle()

            viewModel.uiState.test {
                assertEquals(
                    initialState.copy(isLoading = true),
                    awaitItem()
                )
            }
        }
    }

    @Disabled
    @Test
    fun `WHEN Authenticate event with correct credentials on state THEN navigate to next step`() {
        runTest {
            whenever(useCaseAuthenticateMock.invoke(any(), any())).thenReturn(success(Unit))
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = AuthenticationViewModel(
                initialState,
                useCaseAuthenticateMock
            )

            viewModel.setAction(AuthenticationViewAction.Authenticate)

            advanceUntilIdle()

            viewModel.uiState.testFix {
                assertEquals(
                    initialState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(
                        isLoading = false,
                        isUserAuthorized = true
                    ),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN Authenticate event with incorrect credentials THEN validate loading and error state sequence`() {
        runTest {
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, error = null)
            val viewModel = AuthenticationViewModel(
                initialState,
                useCaseAuthenticateMock
            )
            whenever(useCaseAuthenticateMock.invoke(any(), any())).thenReturn(failure(AuthenticationError.WrongAuthentication))

            viewModel.setAction(AuthenticationViewAction.Authenticate)

            advanceUntilIdle()

            viewModel.uiState.test {
                assertEquals(
                    initialState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(
                        isLoading = false,
                        error = R.string.authentication_auth_error,
                        isUserAuthorized = false
                    ),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    companion object {
        private const val EMAIL = "my@email.com"
        private const val PASSWORD = "123456789Aab"
    }
}
