package com.alxnophis.jetpack.authentication.ui.viewmodel

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
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import kotlinx.coroutines.launch

internal class AuthenticationViewModel(
    initialState: AuthenticationState,
    private val authenticateUseCase: AuthenticateUseCase
) : BaseViewModel<AuthenticationEvent, AuthenticationState>(initialState) {

    override fun handleEvent(event: AuthenticationEvent) {
        viewModelScope.launch {
            when (event) {
                AuthenticationEvent.Authenticate -> authenticate()
                AuthenticationEvent.ErrorDismissed -> updateState { copy(error = null) }
                AuthenticationEvent.ToggleAuthenticationMode -> toggleAuthenticationMode()
                AuthenticationEvent.SetUserNotAuthorized -> updateState { copy(isUserAuthorized = false) }
                AuthenticationEvent.AutoCompleteAuthorization -> updateState {
                    copy(email = AUTHORIZED_EMAIL, password = AUTHORIZED_PASSWORD)
                }
                is AuthenticationEvent.EmailChanged -> updateEmail(event.email)
                is AuthenticationEvent.PasswordChanged -> updatePassword(event.password)
            }
        }
    }

    private fun toggleAuthenticationMode() {
        val newAuthenticationMode = when (currentState.authenticationMode) {
            AuthenticationMode.SIGN_IN -> AuthenticationMode.SIGN_UP
            else -> AuthenticationMode.SIGN_IN
        }
        updateState {
            copy(
                authenticationMode = newAuthenticationMode,
                email = EMPTY,
                password = EMPTY
            )
        }
    }

    private fun updateEmail(email: String) {
        viewModelScope.launch {
            updateState {
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
            updateState {
                copy(
                    password = newPassword,
                    passwordRequirements = requirements.toList()
                )
            }
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            authenticateUser(currentState.email, currentState.password).fold(
                {
                    updateState {
                        copy(
                            isLoading = false,
                            error = R.string.authentication_auth_error
                        )
                    }
                },
                {
                    updateState {
                        copy(
                            isLoading = false,
                            isUserAuthorized = true
                        )
                    }
                }
            )
        }
    }

    private suspend fun authenticateUser(email: String, password: String): Either<AuthenticationError, Unit> =
        authenticateUseCase.invoke(email, password)

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
