package com.alxnophis.jetpack.settings.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.settings.di.injectSettings
import com.alxnophis.jetpack.settings.ui.composable.SettingsScreen
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiEvent
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsFeature(onBack: () -> Unit) {
    injectSettings()
    val viewModel = koinViewModel<SettingsViewModel>()
    SettingsScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                SettingsUiEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
