package com.alxnophis.jetpack.settings.ui.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.router.screen.SETTINGS_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import com.alxnophis.jetpack.settings.di.injectSettings
import com.alxnophis.jetpack.settings.ui.view.SettingsScreen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Settings.route,
        route = SETTINGS_ROUTE
    ) {
        composable(
            route = Screen.Settings.route,
        ) {
            injectSettings()
            SettingsScreen(
                navController = navController,
                viewModel = getViewModel()
            )
        }
    }
}
