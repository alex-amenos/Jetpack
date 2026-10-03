package com.alxnophis.jetpack.authentication.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AuthenticationScreen(
    state: AuthenticationState,
    onNavigateToAuthorized: (email: String) -> Unit = {},
    onBack: () -> Unit = {},
    onEvent: (AuthenticationEvent) -> Unit = {},
) {
    LaunchedEffect(state.isUserAuthorized) {
        if (state.isUserAuthorized) {
            onNavigateToAuthorized(state.email)
            onEvent(AuthenticationEvent.SetUserNotAuthorized)
        }
    }
    AuthenticationContent(
        authenticationState = state,
        onBack = onBack,
        onEvent = onEvent,
    )
}

@ExperimentalComposeUiApi
@Composable
internal fun AuthenticationContent(
    authenticationState: AuthenticationState,
    onBack: () -> Unit = {},
    onEvent: (AuthenticationEvent) -> Unit = {},
) {
    AppTheme {
        AuthenticationForm(
            authenticationMode = authenticationState.authenticationMode,
            isLoading = authenticationState.isLoading,
            email = authenticationState.email,
            password = authenticationState.password,
            completedPasswordRequirements = authenticationState.passwordRequirements,
            enableAuthentication =
                if (authenticationState.isFormValid()) {
                    !authenticationState.isLoading
                } else {
                    false
                },
            onBack = onBack,
            handleEvent = onEvent,
        )
        authenticationState.error?.let { error ->
            CoreErrorDialog(
                errorMessage = stringResource(error.messageRes),
                dismissError = { onEvent(AuthenticationEvent.ErrorDismissRequested(error.id)) },
            )
        }
    }
}

@PreviewLightDark
@ExperimentalComposeUiApi
@Composable
private fun AuthenticationFormPreview() {
    AuthenticationContent(AuthenticationState.initialState)
}
