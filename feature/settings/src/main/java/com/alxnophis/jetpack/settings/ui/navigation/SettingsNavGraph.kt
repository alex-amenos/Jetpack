package com.alxnophis.jetpack.settings.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.settings.ui.composable.SettingsScreen
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsFeature(onBack: () -> Unit) {
    val viewModel = koinViewModel<SettingsViewModel>()
    SettingsScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onBack = onBack,
        onEvent = viewModel::handleEvent,
    )
}
