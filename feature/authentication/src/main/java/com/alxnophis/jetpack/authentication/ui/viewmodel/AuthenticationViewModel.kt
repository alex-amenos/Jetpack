package com.alxnophis.jetpack.authentication.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEffect
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationViewAction
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class AuthenticationViewModel(
    initialState: AuthenticationState = AuthenticationState(),
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val useCaseAuthenticate: UseCaseAuthenticate,
) : BaseViewModel<AuthenticationViewAction, AuthenticationState, AuthenticationEffect>(initialState) {

    override fun handleAction(viewAction: AuthenticationViewAction) =
        when (viewAction) {
            AuthenticationViewAction.Authenticate -> authenticate()
            AuthenticationViewAction.ErrorDismissed -> dismissError()
            AuthenticationViewAction.ToggleAuthenticationMode -> toggleAuthenticationMode()
            is AuthenticationViewAction.EmailChanged -> updateEmail(viewAction.email)
            is AuthenticationViewAction.PasswordChanged -> updatePassword(viewAction.password)
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
        setState {
            copy(
                password = newPassword,
                passwordRequirements = requirements.toList()
            )
        }
    }

    private fun dismissError() {
        setState {
            copy(error = null)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            setState {
                copy(isLoading = true)
            }
            authenticateUser(currentState.email, currentState.password).fold(
                {
                    setState {
                        copy(isLoading = false)
                    }
                    setEffect {
                        AuthenticationEffect.UserAuthorized
                    }
                },
                {
                    setState {
                        copy(
                            isLoading = false,
                            error = R.string.authentication_auth_error,
                        )
                    }
                }
            )
        }
    }

    private suspend fun authenticateUser(email: String, password: String): Result<Unit> =
        withContext(dispatcherIO) {
            useCaseAuthenticate.invoke(email, password)
        }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
