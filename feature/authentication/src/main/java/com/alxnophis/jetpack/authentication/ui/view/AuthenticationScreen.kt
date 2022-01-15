package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.core.ui.theme.CoreTheme

@ExperimentalComposeUiApi
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
        authenticationState.error?.let { error ->
            AuthenticationErrorDialog(
                errorRes = error,
                dismissError = {
                    handleEvent(
                        AuthenticationEvent.ErrorDismissed
                    )
                }
            )
        }
    }
}

@Composable
fun AuthenticationErrorDialog(
    modifier: Modifier = Modifier,
    errorRes: Int,
    dismissError: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            dismissError()
        },
        buttons = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, end = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                TextButton(
                    onClick = {
                        dismissError()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.authentication_error_action),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(R.string.authentication_error_title),
                fontSize = 18.sp
            )
        },
        text = {
            Text(text = stringResource(errorRes))
        }
    )
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
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
