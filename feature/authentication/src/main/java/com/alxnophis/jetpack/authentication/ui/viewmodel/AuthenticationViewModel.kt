package com.alxnophis.jetpack.authentication.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.copy
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
import com.alxnophis.jetpack.authentication.ui.contract.authenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.email
import com.alxnophis.jetpack.authentication.ui.contract.error
import com.alxnophis.jetpack.authentication.ui.contract.isLoading
import com.alxnophis.jetpack.authentication.ui.contract.isUserAuthorized
import com.alxnophis.jetpack.authentication.ui.contract.password
import com.alxnophis.jetpack.authentication.ui.contract.passwordRequirements
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import kotlinx.coroutines.launch

internal class AuthenticationViewModel(
    private val authenticateUseCase: AuthenticateUseCase,
    initialState: AuthenticationState = AuthenticationState.initialState
) : BaseViewModel<AuthenticationEvent, AuthenticationState>(initialState) {

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
            updateUiState {
                copy {
                    AuthenticationState.email set email
                }
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
            updateUiState {
                copy {
                    AuthenticationState.password set newPassword
                    AuthenticationState.passwordRequirements set requirements.toList()
                }
            }
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            updateUiState {
                copy {
                    AuthenticationState.isLoading set true
                }
            }
            authenticateUser(currentState.email, currentState.password).fold(
                {
                    updateUiState {
                        copy {
                            AuthenticationState.isLoading set false
                            AuthenticationState.error set R.string.authentication_auth_error
                        }
                    }
                },
                {
                    updateUiState {
                        copy {
                            AuthenticationState.isLoading set false
                            AuthenticationState.isUserAuthorized set true
                        }
                    }
                }
            )
        }
    }

    private fun dismissError() {
        viewModelScope.launch {
            updateUiState {
                copy {
                    AuthenticationState.error set NO_ERROR
                }
            }
        }
    }

    private fun toggleAuthenticationMode() {
        val newAuthenticationMode = when (currentState.authenticationMode) {
            AuthenticationMode.SIGN_IN -> AuthenticationMode.SIGN_UP
            else -> AuthenticationMode.SIGN_IN
        }
        updateUiState {
            copy {
                AuthenticationState.authenticationMode set newAuthenticationMode
                AuthenticationState.email set EMPTY
                AuthenticationState.password set EMPTY
            }
        }
    }

    private fun setUserNotAuthorized() {
        viewModelScope.launch {
            updateUiState {
                copy {
                    AuthenticationState.isUserAuthorized set false
                }
            }
        }
    }

    private fun autoCompleteAuthorization() {
        viewModelScope.launch {
            updateUiState {
                copy {
                    AuthenticationState.email set AUTHORIZED_EMAIL
                    AuthenticationState.password set AUTHORIZED_PASSWORD
                }
            }
        }
    }

    private suspend fun authenticateUser(email: String, password: String): Either<AuthenticationError, Unit> =
        authenticateUseCase.invoke(email, password)

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
