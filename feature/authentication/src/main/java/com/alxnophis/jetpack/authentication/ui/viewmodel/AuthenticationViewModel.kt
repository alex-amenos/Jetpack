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
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class AuthenticationViewModel(
    initialState: AuthenticationState,
    private val dispatchers: DispatcherProvider,
    private val authenticateUseCase: AuthenticateUseCase,
) : BaseViewModel<AuthenticationEvent, AuthenticationState>(initialState) {

    override fun handleEvent(event: AuthenticationEvent) {
        viewModelScope.launch {
            when (event) {
                AuthenticationEvent.Authenticate -> authenticate()
                AuthenticationEvent.ErrorDismissed -> updateState { copy(error = null) }
                AuthenticationEvent.ToggleAuthenticationMode -> toggleAuthenticationMode()
                AuthenticationEvent.UserAuthorizedEventConsumed -> updateState { copy(userAuthorizedEvent = consumed) }
                AuthenticationEvent.AutoCompleteAuthorization -> updateState {
                    copy(email = AUTHORIZED_EMAIL, password = AUTHORIZED_PASSWORD)
                }
                is AuthenticationEvent.EmailChanged -> updateState { copy(email = event.email) }
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
                            error = R.string.authentication_auth_error,
                        )
                    }
                },
                {
                    updateState {
                        copy(
                            isLoading = false,
                            userAuthorizedEvent = triggered,
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
