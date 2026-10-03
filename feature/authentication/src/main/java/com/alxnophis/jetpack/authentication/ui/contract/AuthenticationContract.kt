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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

private object ImmutablePasswordRequirementsListParceler : Parceler<ImmutableList<PasswordRequirements>> by immutableListParceler()

internal sealed class AuthenticationEvent : UiEvent {
    data object Authenticated : AuthenticationEvent()

    data object AutoCompleteAuthorizationRequested : AuthenticationEvent()

    data class ErrorDismissRequested(
        val errorId: Long = 0L,
    ) : AuthenticationEvent()

    data object ToggleAuthenticationModeRequested : AuthenticationEvent()

    data object SetUserNotAuthorized : AuthenticationEvent()

    data class EmailChanged(
        val email: String,
    ) : AuthenticationEvent()

    data class PasswordChanged(
        val password: String,
    ) : AuthenticationEvent()
}

@Parcelize
@Immutable
internal data class AuthenticationUiError(
    val id: Long = System.currentTimeMillis(),
    @param:StringRes val messageRes: Int,
) : Parcelable

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
    val error: AuthenticationUiError?,
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
                passwordRequirements = persistentListOf(),
                isLoading = false,
                error = null,
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
