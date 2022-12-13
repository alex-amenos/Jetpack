package com.alxnophis.jetpack.authentication.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.router.screen.Screen
import de.palm.composestateevents.EventEffect
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AuthenticationScreen(
    navController: NavController,
    viewModel: AuthenticationViewModel = getViewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    AuthenticationContent(
        state,
        viewModel::handleEvent
    )
    BackHandler {
        navController.popBackStack()
    }
    EventEffect(
        event = state.userAuthorizedEvent,
        onConsumed = { viewModel.handleEvent(AuthenticationEvent.UserAuthorizedEventConsumed) }
    ) {
        navController.navigate(Screen.Authorized.routeWithParams(state.email)) {
            // Remove Authentication screen form back stack
            popUpTo(Screen.Authentication.route) {
                inclusive = true
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
internal fun AuthenticationContent(
    authenticationState: AuthenticationState,
    handleEvent: AuthenticationEvent.() -> Unit,
) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface),
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
