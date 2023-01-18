package com.alxnophis.jetpack.authentication.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AuthenticationScreen(
    navigateNextStep: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: AuthenticationViewModel = getViewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    LaunchedEffect(state.isUserAuthorized) {
        if (state.isUserAuthorized) {
            navigateNextStep(state.email)
            viewModel.handleEvent(AuthenticationEvent.UserAuthorizedConsumed)
        }
    }
    BackHandler {
        popBackStack()
    }
    AuthenticationContent(
        state,
        viewModel::handleEvent
    )
}

@ExperimentalComposeUiApi
@Composable
internal fun AuthenticationContent(
    authenticationState: AuthenticationState,
    handleEvent: AuthenticationEvent.() -> Unit,
) {
    AppTheme {
        AuthenticationForm(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxSize(),
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
            handleEvent = handleEvent
        )
        authenticationState.error?.let { error: Int ->
            CoreErrorDialog(
                errorMessage = stringResource(error),
                dismissError = {
                    handleEvent(AuthenticationEvent.ErrorDismissed)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun AuthenticationFormPreview() {
    AuthenticationContent(
        authenticationState = AuthenticationState(),
        handleEvent = {},
    )
}
