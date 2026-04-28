package com.alxnophis.jetpack.authentication.ui.contract

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.ui.parceler.immutableListParceler
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

internal const val NO_ERROR = 0

internal object ImmutablePasswordRequirementsListParceler : Parceler<ImmutableList<PasswordRequirements>> by immutableListParceler()

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

@Parcelize
@Immutable
@TypeParceler<ImmutableList<PasswordRequirements>, ImmutablePasswordRequirementsListParceler>()
internal data class AuthenticationState(
    val isUserAuthorized: Boolean,
    val authenticationMode: AuthenticationMode,
    val email: String,
    val password: String,
    val passwordRequirements: ImmutableList<PasswordRequirements>,
    val isLoading: Boolean,
    val error: Int,
) : UiState,
    Parcelable {
    fun isFormValid(): Boolean =
        password.isNotEmpty() && email.isNotEmpty() && (
            authenticationMode == AuthenticationMode.SIGN_IN ||
                passwordRequirements.containsAll(
                    PasswordRequirements.entries.toList(),
                )
        )

    internal companion object {
        val initialState =
            AuthenticationState(
                isUserAuthorized = false,
                authenticationMode = AuthenticationMode.SIGN_IN,
                email = EMPTY,
                password = EMPTY,
                passwordRequirements = emptyList<PasswordRequirements>().toImmutableList(),
                isLoading = false,
                error = NO_ERROR,
            )
    }
}

@Parcelize
enum class PasswordRequirements(
    @param:StringRes val label: Int,
) : Parcelable {
    CAPITAL_LETTER(R.string.authentication_requirement_capital),
    NUMBER(R.string.authentication_requirement_digit),
    EIGHT_CHARACTERS(R.string.authentication_requirement_characters),
}

@Parcelize
enum class AuthenticationMode : Parcelable {
    SIGN_UP,
    SIGN_IN,
}
