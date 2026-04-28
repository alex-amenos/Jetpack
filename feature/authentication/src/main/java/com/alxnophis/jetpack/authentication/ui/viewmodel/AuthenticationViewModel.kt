package com.alxnophis.jetpack.authentication.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase.Companion.AUTHORIZED_EMAIL
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase.Companion.AUTHORIZED_PASSWORD
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.NO_ERROR
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class AuthenticationViewModel(
    private val authenticateUseCase: AuthenticateUseCase,
    savedStateHandle: SavedStateHandle,
    initialState: AuthenticationState =
        savedStateHandle
            .get<AuthenticationState>(SAVED_STATE_HANDLE_UI_STATE_KEY)
            ?.copy(password = EMPTY) ?: AuthenticationState.initialState.copy(isLoading = false),
) : BaseViewModel<AuthenticationEvent, AuthenticationState>(initialState, savedStateHandle) {
    /**
     * Never persist the password field — clear it before writing to SavedStateHandle
     * so that sensitive credentials are not stored across process recreation.
     */
    override fun sanitizeForSavedState(state: AuthenticationState): AuthenticationState = state.copy(password = EMPTY)

    override fun handleEvent(event: AuthenticationEvent) {
        viewModelScope.launch {
            when (event) {
                AuthenticationEvent.Authenticated -> authenticate()
                AuthenticationEvent.ErrorDismissRequested -> dismissError()
                AuthenticationEvent.ToggleAuthenticationModeRequested -> toggleAuthenticationMode()
                AuthenticationEvent.SetUserNotAuthorized -> setUserNotAuthorized()
                AuthenticationEvent.AutoCompleteAuthorizationRequested -> autoCompleteAuthorization()
                AuthenticationEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented")
                is AuthenticationEvent.EmailChanged -> updateEmail(event.email)
                is AuthenticationEvent.PasswordChanged -> updatePassword(event.password)
                is AuthenticationEvent.NavigateToAuthScreenRequested -> throw IllegalStateException("Navigate to auth screen not implemented")
            }
        }
    }

    private fun updateEmail(email: String) {
        viewModelScope.launch {
            updateAndPersistUiState {
                copy(email = email)
            }
        }
    }

    private fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            val requirements = mutableListOf<PasswordRequirements>()
            if (newPassword.length >= MIN_PASSWORD_LENGTH) {
                requirements.add(PasswordRequirements.EIGHT_CHARACTERS)
            }
            if (newPassword.any { it.isUpperCase() }) {
                requirements.add(PasswordRequirements.CAPITAL_LETTER)
            }
            if (newPassword.any { it.isDigit() }) {
                requirements.add(PasswordRequirements.NUMBER)
            }
            updateAndPersistUiState {
                copy(
                    password = newPassword,
                    passwordRequirements = requirements.toImmutableList(),
                )
            }
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            updateAndPersistUiState {
                copy(isLoading = true)
            }
            authenticateUser(currentState.email, currentState.password).fold(
                {
                    updateAndPersistUiState {
                        copy(
                            isLoading = false,
                            error = R.string.authentication_auth_error,
                        )
                    }
                },
                {
                    updateAndPersistUiState {
                        copy(
                            isLoading = false,
                            isUserAuthorized = true,
                        )
                    }
                },
            )
        }
    }

    private fun dismissError() {
        updateAndPersistUiState {
            copy(error = NO_ERROR)
        }
    }

    private fun toggleAuthenticationMode() {
        val newAuthenticationMode =
            when (currentState.authenticationMode) {
                AuthenticationMode.SIGN_IN -> AuthenticationMode.SIGN_UP
                else -> AuthenticationMode.SIGN_IN
            }
        updateAndPersistUiState {
            copy(
                authenticationMode = newAuthenticationMode,
                email = EMPTY,
                password = EMPTY,
            )
        }
    }

    private fun setUserNotAuthorized() {
        viewModelScope.launch {
            updateAndPersistUiState {
                copy(isUserAuthorized = false)
            }
        }
    }

    private fun autoCompleteAuthorization() {
        viewModelScope.launch {
            updateAndPersistUiState {
                copy(
                    email = AUTHORIZED_EMAIL,
                    password = AUTHORIZED_PASSWORD,
                )
            }
        }
    }

    private suspend fun authenticateUser(
        email: String,
        password: String,
    ): Either<AuthenticationError, Unit> = authenticateUseCase.invoke(email, password)

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
