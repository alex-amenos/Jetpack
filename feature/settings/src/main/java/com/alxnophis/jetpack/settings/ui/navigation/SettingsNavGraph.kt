package com.alxnophis.jetpack.settings.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.router.screen.Route
import com.alxnophis.jetpack.settings.di.injectSettings
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.view.SettingsScreen
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    composable<Route.Settings> {
        injectSettings()
        val viewModel = getViewModel<SettingsViewModel>()
        SettingsScreen(
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            onEvent = { event ->
                when (event) {
                    SettingsEvent.GoBackRequested -> navController.popBackStack()
                    else -> viewModel.handleEvent(event)
                }
            }
        )
    }
}
