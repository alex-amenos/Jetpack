package com.alxnophis.jetpack.authentication.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase.Companion.AUTHORIZED_EMAIL
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase.Companion.AUTHORIZED_PASSWORD
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
internal class AuthenticationViewModelUnitTest : BaseViewModelUnitTest() {

    private lateinit var viewModel: AuthenticationViewModel

    @BeforeEach
    override fun beforeEach() {
        super.beforeEach()
        viewModel = viewModelMother()
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
            viewModel.setEvent(AuthenticationEvent.ToggleAuthenticationMode)

            viewModel.uiState.test {
                assertEquals(
                    initialAuthenticationState,
                    awaitItem()
                )
                assertEquals(
                    initialAuthenticationState.copy(authenticationMode = AuthenticationMode.SIGN_UP),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN signUp state and ToggleAuthenticationMode event THEN validate state is signIn`() {
        runTest {
            val initialState = AuthenticationState().copy(authenticationMode = AuthenticationMode.SIGN_UP)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.setEvent(AuthenticationEvent.ToggleAuthenticationMode)

            viewModel.uiState.test {
                assertEquals(
                    initialState,
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(authenticationMode = AuthenticationMode.SIGN_IN),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN error state and ErrorDismissed event THEN validate dismissed error`() {
        runTest {
            val initialState = AuthenticationState().copy(error = 1)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.setEvent(AuthenticationEvent.ErrorDismissed)

            viewModel.uiState.test {
                assertEquals(
                    initialState,
                    awaitItem()
                )
                assertEquals(
                    initialAuthenticationState.copy(error = null),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN updated email and EmailChanged event THEN validate state change`() {
        runTest {
            viewModel.setEvent(AuthenticationEvent.EmailChanged(EMAIL))

            viewModel.uiState.test {
                assertEquals(
                    initialAuthenticationState,
                    awaitItem()
                )
                assertEquals(
                    initialAuthenticationState.copy(email = EMAIL),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN updated password and PasswordChanged event THEN validate state change`() {
        runTest {
            viewModel.setEvent(AuthenticationEvent.PasswordChanged(PASSWORD))

            viewModel.uiState.test {
                assertEquals(
                    initialAuthenticationState,
                    awaitItem()
                )
                assertEquals(
                    initialAuthenticationState.copy(
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
            whenever(authenticateUseCaseMock.invoke(any(), any())).thenReturn(Unit.right())
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.setEvent(AuthenticationEvent.Authenticate)

            viewModel.uiState.test {
                assertEquals(
                    initialState,
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(isLoading = true),
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `WHEN Authenticate event with correct credentials THEN update state accordingly`() {
        runTest {
            whenever(authenticateUseCaseMock.invoke(any(), any())).thenReturn(Unit.right())
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.setEvent(AuthenticationEvent.Authenticate)

            viewModel.uiState.test {
                assertEquals(
                    initialState,
                    awaitItem()
                )
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
            val viewModel = viewModelMother(initialState = initialState)
            whenever(authenticateUseCaseMock.invoke(EMAIL, PASSWORD)).thenReturn(AuthenticationError.WrongAuthentication.left())

            viewModel.setEvent(AuthenticationEvent.Authenticate)

            viewModel.uiState.test {
                assertEquals(
                    initialState,
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    initialState.copy(
                        isLoading = false,
                        error = R.string.authentication_auth_error,
                    ),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    private fun viewModelMother(
        initialState: AuthenticationState = initialAuthenticationState,
        authenticateUseCase: AuthenticateUseCase = authenticateUseCaseMock
    ) = AuthenticationViewModel(
        initialState,
        testDispatcherProvider,
        authenticateUseCase
    )

    companion object {
        private const val EMAIL = AUTHORIZED_EMAIL
        private const val PASSWORD = AUTHORIZED_PASSWORD
        private val authenticateUseCaseMock: AuthenticateUseCase = mock()
        private val initialAuthenticationState = AuthenticationState()
    }
}
