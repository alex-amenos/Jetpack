package com.alxnophis.jetpack.authentication.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.NO_ERROR
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AuthenticationScreen(
    state: AuthenticationState,
    onEvent: (AuthenticationEvent) -> Unit = {},
) {
    LaunchedEffect(state.isUserAuthorized) {
        if (state.isUserAuthorized) {
            onEvent(AuthenticationEvent.NavigateToAuthScreenRequested(state.email))
            onEvent(AuthenticationEvent.SetUserNotAuthorized)
        }
    }
    BackHandler { onEvent(AuthenticationEvent.GoBackRequested) }
    AuthenticationContent(state, onEvent)
}

@ExperimentalComposeUiApi
@Composable
internal fun AuthenticationContent(
    authenticationState: AuthenticationState,
    onEvent: AuthenticationEvent.() -> Unit = {},
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
            handleEvent = onEvent,
        )
        if (authenticationState.error != NO_ERROR) {
            CoreErrorDialog(
                errorMessage = stringResource(authenticationState.error),
                dismissError = { onEvent(AuthenticationEvent.ErrorDismissRequested) },
            )
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun AuthenticationFormPreview() {
    AuthenticationContent(AuthenticationState.initialState)
}
