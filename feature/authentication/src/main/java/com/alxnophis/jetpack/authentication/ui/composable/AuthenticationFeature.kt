package com.alxnophis.jetpack.authentication.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthenticationFeature(
    navigateInCaseOfSuccess: (email: String) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<AuthenticationViewModel>()
    AuthenticationScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onNavigateToAuthorized = navigateInCaseOfSuccess,
        onBack = onBack,
        onEvent = viewModel::handleEvent,
    )
}
