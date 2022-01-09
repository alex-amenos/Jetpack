package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.core.ui.theme.CoreTheme

@Composable
internal fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    authenticationState: AuthenticationState,
    handleEvent: (event: AuthenticationEvent) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AuthenticationForm(
            modifier = Modifier.fillMaxSize(),
            authenticationMode = authenticationState.authenticationMode,
            isLoading = authenticationState.isLoading,
            email = authenticationState.email,
            password = authenticationState.password,
            completedPasswordRequirements = authenticationState.passwordRequirements,
            enableAuthentication = if (authenticationState.isFormValid()) {
                !authenticationState.isLoading
            } else {
                false
            },
            onEmailChanged = { email ->
                handleEvent(AuthenticationEvent.EmailChanged(email))
            },
            onPasswordChanged = { password ->
                handleEvent(AuthenticationEvent.PasswordChanged(password))
            },
            onAuthenticate = {
                handleEvent(AuthenticationEvent.Authenticate)
            },
            onToggleMode = {
                handleEvent(AuthenticationEvent.ToggleAuthenticationMode)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthenticationFormPreview() {
    val previewState = AuthenticationState(isLoading = true)
    CoreTheme {
        AuthenticationScreen(
            modifier = Modifier.fillMaxSize(),
            authenticationState = previewState,
            handleEvent = {}
        )
    }
}
