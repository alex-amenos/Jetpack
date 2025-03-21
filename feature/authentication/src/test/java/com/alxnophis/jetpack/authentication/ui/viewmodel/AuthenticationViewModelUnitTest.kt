package com.alxnophis.jetpack.authentication.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.NO_ERROR
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
private class AuthenticationViewModelUnitTest : BaseViewModelUnitTest() {
    private lateinit var viewModel: AuthenticationViewModel

    override fun beforeEachCompleted() {
        viewModel = viewModelMother()
    }

    @Test
    fun `WHEN init THEN validate initial state`() {
        runTest {
            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo AuthenticationState.initialState
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN a not authorized user with signIn mode WHEN change the authentication mode THEN validate state is signUp`() {
        runTest {
            viewModel.handleEvent(AuthenticationEvent.ToggleAuthenticationModeRequested)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo AuthenticationState.initialState
                awaitItem() shouldBeEqualTo AuthenticationState.initialState.copy(authenticationMode = AuthenticationMode.SIGN_UP)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN a not authorized user with signUp mode WHEN change the authentication mode THEN validate state is signIn`() {
        runTest {
            val initialState = AuthenticationState.initialState.copy(authenticationMode = AuthenticationMode.SIGN_UP)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.handleEvent(AuthenticationEvent.ToggleAuthenticationModeRequested)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo initialState
                awaitItem() shouldBeEqualTo initialState.copy(authenticationMode = AuthenticationMode.SIGN_IN)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN a error state WHEN request dismiss error THEN validate dismissed error`() {
        runTest {
            val initialState = AuthenticationState.initialState.copy(error = 1)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.handleEvent(AuthenticationEvent.ErrorDismissRequested)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo initialState
                awaitItem() shouldBeEqualTo initialState.copy(error = NO_ERROR)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN email changes THEN validate state change`() {
        runTest {
            viewModel.handleEvent(AuthenticationEvent.EmailChanged(EMAIL))

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo AuthenticationState.initialState
                awaitItem() shouldBeEqualTo AuthenticationState.initialState.copy(email = EMAIL)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN password changes THEN validate state change`() {
        runTest {
            viewModel.handleEvent(AuthenticationEvent.PasswordChanged(PASSWORD))

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo AuthenticationState.initialState
                awaitItem() shouldBeEqualTo
                    AuthenticationState.initialState.copy(
                        password = PASSWORD,
                        passwordRequirements =
                            listOf(
                                PasswordRequirements.EIGHT_CHARACTERS,
                                PasswordRequirements.CAPITAL_LETTER,
                                PasswordRequirements.NUMBER,
                            ),
                    )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN user is authenticated with correct credentials THEN update state accordingly`() {
        runTest {
            whenever(authenticateUseCaseMock.invoke(any(), any())).thenReturn(Unit.right())
            val initialState = AuthenticationState.initialState.copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.handleEvent(AuthenticationEvent.Authenticated)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo initialState
                awaitItem() shouldBeEqualTo initialState.copy(isLoading = true)
                awaitItem() shouldBeEqualTo
                    initialState.copy(
                        isLoading = false,
                        isUserAuthorized = true,
                    )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN an authorized user WHEN remove user authorization THEN update state accordingly`() {
        runTest {
            whenever(authenticateUseCaseMock.invoke(any(), any())).thenReturn(Unit.right())
            val initialState = AuthenticationState.initialState.copy(email = EMAIL, password = PASSWORD, isLoading = false, isUserAuthorized = true)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.handleEvent(AuthenticationEvent.SetUserNotAuthorized)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo initialState
                awaitItem() shouldBeEqualTo initialState.copy(isUserAuthorized = false)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN a not authorized user WHEN authenticated an user with incorrect credentials THEN validate loading and error state sequence`() {
        runTest {
            val initialState = AuthenticationState.initialState.copy(email = EMAIL, password = PASSWORD, error = NO_ERROR)
            val viewModel = viewModelMother(initialState = initialState)
            whenever(authenticateUseCaseMock.invoke(EMAIL, PASSWORD)).thenReturn(AuthenticationError.WrongAuthentication.left())

            viewModel.handleEvent(AuthenticationEvent.Authenticated)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo initialState
                awaitItem() shouldBeEqualTo initialState.copy(isLoading = true)
                awaitItem() shouldBeEqualTo
                    initialState.copy(
                        isLoading = false,
                        error = R.string.authentication_auth_error,
                    )
                expectNoEvents()
            }
        }
    }

    private fun viewModelMother(
        initialState: AuthenticationState = AuthenticationState.initialState,
        authenticateUseCase: AuthenticateUseCase = authenticateUseCaseMock,
    ) = AuthenticationViewModel(
        authenticateUseCase,
        initialState,
    )

    companion object {
        private const val EMAIL = "my@email.com"
        private const val PASSWORD = "12345678Aab"
        private val authenticateUseCaseMock: AuthenticateUseCase = mock()
    }
}
