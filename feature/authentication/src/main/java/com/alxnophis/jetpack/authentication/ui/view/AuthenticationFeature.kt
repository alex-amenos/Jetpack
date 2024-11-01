package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun AuthenticationFeature(
    navigateInCaseOfSuccess: (email: String) -> Unit,
    onBack: () -> Unit,
) {
    injectAuthentication()
    val viewModel = getViewModel<AuthenticationViewModel>()
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
