package com.alxnophis.jetpack.authentication.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthenticationFeature(
    navigateInCaseOfSuccess: (email: String) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<AuthenticationViewModel>()
    AuthenticationScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                AuthenticationEvent.GoBackRequested -> onBack()
                is AuthenticationEvent.NavigateToAuthScreenRequested -> {
                    navigateInCaseOfSuccess(event.email)
                }

                else -> viewModel.handleEvent(event)
            }
        },
    )
}
