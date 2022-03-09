package com.alxnophis.jetpack.authentication.ui.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate.Companion.AUTHORIZED_EMAIL
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate.Companion.AUTHORIZED_PASSWORD
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEffect
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationViewAction
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.testing.base.BaseViewModel5UnitTest
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
internal class AuthenticationViewModelUnitTest : BaseViewModel5UnitTest() {

    private lateinit var viewModel: AuthenticationViewModel

    @BeforeEach
    fun beforeEach() {
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
            viewModel.setAction(AuthenticationViewAction.ToggleAuthenticationMode)

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

            viewModel.setAction(AuthenticationViewAction.ToggleAuthenticationMode)

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

            viewModel.setAction(AuthenticationViewAction.ErrorDismissed)

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
            viewModel.setAction(AuthenticationViewAction.EmailChanged(EMAIL))

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
            viewModel.setAction(AuthenticationViewAction.PasswordChanged(PASSWORD))

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
            whenever(useCaseAuthenticateMock.invoke(any(), any())).thenReturn(success(Unit))
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.setAction(AuthenticationViewAction.Authenticate)

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
            whenever(useCaseAuthenticateMock.invoke(any(), any())).thenReturn(success(Unit))
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.setAction(AuthenticationViewAction.Authenticate)

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
                    initialState.copy(isLoading = false),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN Authenticate event with correct credentials THEN navigate to next step`() {
        runTest {
            whenever(useCaseAuthenticateMock.invoke(any(), any())).thenReturn(success(Unit))
            val initialState = AuthenticationState().copy(email = EMAIL, password = PASSWORD, isLoading = false)
            val viewModel = viewModelMother(initialState = initialState)

            viewModel.setAction(AuthenticationViewAction.Authenticate)

            viewModel.effect.test {
                assertEquals(
                    AuthenticationEffect.UserAuthorized,
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
            whenever(useCaseAuthenticateMock.invoke(EMAIL, PASSWORD)).thenReturn(failure(AuthenticationError.WrongAuthentication))

            viewModel.setAction(AuthenticationViewAction.Authenticate)

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
        dispatcherIO: TestDispatcher = testDispatcher,
        useCaseAuthenticate: UseCaseAuthenticate = useCaseAuthenticateMock
    ) = AuthenticationViewModel(
        initialState,
        dispatcherIO,
        useCaseAuthenticate
    )

    companion object {
        private const val EMAIL = AUTHORIZED_EMAIL
        private const val PASSWORD = AUTHORIZED_PASSWORD
        private val useCaseAuthenticateMock: UseCaseAuthenticate = mock()
        private val initialAuthenticationState = AuthenticationState()
    }
}
