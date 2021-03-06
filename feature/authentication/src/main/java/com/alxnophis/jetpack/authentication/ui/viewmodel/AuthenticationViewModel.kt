package com.alxnophis.jetpack.authentication.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.model.AuthenticationError
import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class AuthenticationViewModel(
    initialState: AuthenticationState,
    private val dispatchers: DispatcherProvider,
    private val authenticateUseCase: AuthenticateUseCase,
) : BaseViewModel<AuthenticationEvent, AuthenticationState>(initialState) {

    override fun handleEvent(event: AuthenticationEvent) =
        when (event) {
            AuthenticationEvent.Authenticate -> authenticate()
            AuthenticationEvent.ErrorDismissed -> dismissError()
            AuthenticationEvent.ToggleAuthenticationMode -> toggleAuthenticationMode()
            is AuthenticationEvent.EmailChanged -> updateEmail(event.email)
            is AuthenticationEvent.PasswordChanged -> updatePassword(event.password)
        }

    private fun toggleAuthenticationMode() {
        val newAuthenticationMode = when (currentState.authenticationMode) {
            AuthenticationMode.SIGN_IN -> AuthenticationMode.SIGN_UP
            else -> AuthenticationMode.SIGN_IN
        }
        setState {
            copy(authenticationMode = newAuthenticationMode)
        }
    }

    private fun updateEmail(newEmail: String) {
        setState {
            copy(email = newEmail)
        }
    }

    private fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            val requirements = mutableListOf<PasswordRequirements>()
            withContext(dispatchers.default()) {
                if (newPassword.length >= MIN_PASSWORD_LENGTH) {
                    requirements.add(PasswordRequirements.EIGHT_CHARACTERS)
                }
                if (newPassword.any { it.isUpperCase() }) {
                    requirements.add(PasswordRequirements.CAPITAL_LETTER)
                }
                if (newPassword.any { it.isDigit() }) {
                    requirements.add(PasswordRequirements.NUMBER)
                }
            }
            setState {
                copy(
                    password = newPassword,
                    passwordRequirements = requirements.toList()
                )
            }
        }
    }

    private fun dismissError() {
        setState { copy(error = null) }
    }

    private fun authenticate() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            authenticateUser(currentState.email, currentState.password).fold(
                {
                    setState {
                        copy(
                            isLoading = false,
                            error = R.string.authentication_auth_error,
                        )
                    }
                },
                {
                    setState {
                        copy(
                            isLoading = false,
                            isUserAuthorized = true,
                        )
                    }
                }
            )
        }
    }

    private suspend fun authenticateUser(email: String, password: String): Either<AuthenticationError, Unit> =
        withContext(dispatchers.io()) {
            authenticateUseCase.invoke(email, password)
        }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
