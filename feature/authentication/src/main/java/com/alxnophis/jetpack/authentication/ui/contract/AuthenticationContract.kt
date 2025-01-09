package com.alxnophis.jetpack.authentication.ui.contract

import androidx.annotation.StringRes
import arrow.optics.optics
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState

internal const val NO_ERROR = 0

internal sealed class AuthenticationEvent : UiEvent {
    data object Authenticated : AuthenticationEvent()

    data object AutoCompleteAuthorizationRequested : AuthenticationEvent()

    data object ErrorDismissRequested : AuthenticationEvent()

    data object ToggleAuthenticationModeRequested : AuthenticationEvent()

    data object SetUserNotAuthorized : AuthenticationEvent()

    data object GoBackRequested : AuthenticationEvent()

    data class EmailChanged(
        val email: String,
    ) : AuthenticationEvent()

    data class PasswordChanged(
        val password: String,
    ) : AuthenticationEvent()

    data class NavigateToAuthScreenRequested(
        val email: String,
    ) : AuthenticationEvent()
}

@optics
internal data class AuthenticationState(
    val isUserAuthorized: Boolean,
    val authenticationMode: AuthenticationMode,
    val email: String,
    val password: String,
    val passwordRequirements: List<PasswordRequirements>,
    val isLoading: Boolean,
    val error: Int,
) : UiState {
    fun isFormValid(): Boolean =
        password.isNotEmpty() &&
            email.isNotEmpty() &&
            (authenticationMode == AuthenticationMode.SIGN_IN || passwordRequirements.containsAll(PasswordRequirements.values().toList()))

    internal companion object {
        val initialState =
            AuthenticationState(
                isUserAuthorized = false,
                authenticationMode = AuthenticationMode.SIGN_IN,
                email = EMPTY,
                password = EMPTY,
                passwordRequirements = emptyList(),
                isLoading = false,
                error = NO_ERROR,
            )
    }
}

enum class PasswordRequirements(
    @StringRes val label: Int,
) {
    CAPITAL_LETTER(R.string.authentication_requirement_capital),
    NUMBER(R.string.authentication_requirement_digit),
    EIGHT_CHARACTERS(R.string.authentication_requirement_characters),
}

enum class AuthenticationMode {
    SIGN_UP,
    SIGN_IN,
}
