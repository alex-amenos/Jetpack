package com.alxnophis.jetpack.authentication.ui.contract

import androidx.annotation.StringRes
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.core.base.viewmodel.UiAction
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class AuthenticationViewAction : UiAction {
    object Authenticate : AuthenticationViewAction()
    object ErrorDismissed : AuthenticationViewAction()
    object ToggleAuthenticationMode : AuthenticationViewAction()
    data class EmailChanged(val email: String) : AuthenticationViewAction()
    data class PasswordChanged(val password: String) : AuthenticationViewAction()
}

internal data class AuthenticationState(
    val authenticationMode: AuthenticationMode = AuthenticationMode.SIGN_IN,
    val email: String = "",
    val password: String = "",
    val passwordRequirements: List<PasswordRequirements> = emptyList(),
    val isLoading: Boolean = false,
    val error: Int? = null,
    val isUserAuthorized: Boolean = false
) : UiState {

    fun isFormValid(): Boolean {
        return password.isNotEmpty() &&
            email.isNotEmpty() &&
            (authenticationMode == AuthenticationMode.SIGN_IN || passwordRequirements.containsAll(PasswordRequirements.values().toList()))
    }
}

enum class PasswordRequirements(@StringRes val label: Int) {
    CAPITAL_LETTER(R.string.authentication_requirement_capital),
    NUMBER(R.string.authentication_requirement_digit),
    EIGHT_CHARACTERS(R.string.authentication_requirement_characters)
}

enum class AuthenticationMode {
    SIGN_UP,
    SIGN_IN
}
