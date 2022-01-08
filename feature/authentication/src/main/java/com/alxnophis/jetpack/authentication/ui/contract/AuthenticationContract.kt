package com.alxnophis.jetpack.authentication.ui.contract

import androidx.annotation.StringRes
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class AuthenticationEvent : UiEvent

internal sealed class AuthenticationEffect : UiEffect

internal data class AuthenticationState(
    val authenticationMode: AuthenticationMode = AuthenticationMode.SIGN_IN,
    val email: String? = null,
    val password: String? = null,
    val passwordRequirements: List<PasswordRequirements> = emptyList(),
    val isLoading: Boolean = false,
    val error: Int? = null
) : UiState {

    fun isFormValid(): Boolean {
        return password?.isNotEmpty() == true &&
                email?.isNotEmpty() == true &&
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
