package com.alxnophis.jetpack.settings.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.router.screen.SETTINGS_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import com.alxnophis.jetpack.settings.di.injectSettings
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.view.SettingsScreen
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Settings.route,
        route = SETTINGS_ROUTE
    ) {
        composable(
            route = Screen.Settings.route
        ) {
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
}
