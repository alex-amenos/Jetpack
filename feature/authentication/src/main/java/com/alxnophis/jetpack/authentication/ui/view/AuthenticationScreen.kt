package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationViewAction
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import org.koin.androidx.compose.getViewModel

@ExperimentalComposeUiApi
@Composable
internal fun AuthenticationComposable(
    viewModel: AuthenticationViewModel = getViewModel()
) {
    CoreTheme {
        val state = viewModel.uiState.collectAsState().value
        AuthenticationScreen(
            state,
            viewModel::setAction
        )
    }
}

@ExperimentalComposeUiApi
@Composable
internal fun AuthenticationScreen(
    authenticationState: AuthenticationState,
    onViewAction: (viewAction: AuthenticationViewAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
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
                onViewAction(AuthenticationViewAction.EmailChanged(email))
            },
            onPasswordChanged = { password ->
                onViewAction(AuthenticationViewAction.PasswordChanged(password))
            },
            onAuthenticate = {
                onViewAction(AuthenticationViewAction.Authenticate)
            },
            onToggleMode = {
                onViewAction(AuthenticationViewAction.ToggleAuthenticationMode)
            }
        )
        authenticationState.error?.let { error: Int ->
            CoreErrorDialog(
                errorMessage = stringResource(error),
                dismissError = {
                    onViewAction(AuthenticationViewAction.ErrorDismissed)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun AuthenticationFormPreview() {
    CoreTheme {
        AuthenticationScreen(
            authenticationState = AuthenticationState(),
            onViewAction = {},
        )
    }
}
