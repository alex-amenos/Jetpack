package com.alxnophis.jetpack.authentication.ui.contract

import androidx.annotation.StringRes
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

internal sealed class AuthenticationEvent : UiEvent {
    object Authenticate : AuthenticationEvent()
    object AutoCompleteAuthorization : AuthenticationEvent()
    object ErrorDismissed : AuthenticationEvent()
    object ToggleAuthenticationMode : AuthenticationEvent()
    object UserAuthorizedEventConsumed : AuthenticationEvent()
    data class EmailChanged(val email: String) : AuthenticationEvent()
    data class PasswordChanged(val password: String) : AuthenticationEvent()
}

internal data class AuthenticationState(
    val userAuthorizedEvent: StateEvent = consumed,
    val authenticationMode: AuthenticationMode = AuthenticationMode.SIGN_IN,
    val email: String = EMPTY,
    val password: String = EMPTY,
    val passwordRequirements: List<PasswordRequirements> = emptyList(),
    val isLoading: Boolean = false,
    val error: Int? = null,
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
