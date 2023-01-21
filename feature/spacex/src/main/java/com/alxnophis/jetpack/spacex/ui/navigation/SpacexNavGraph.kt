package com.alxnophis.jetpack.spacex.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.router.screen.SPACEX_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import com.alxnophis.jetpack.spacex.di.injectSpacex
import com.alxnophis.jetpack.spacex.ui.view.SpacexScreen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.spacexNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.Spacex.route,
        route = SPACEX_ROUTE
    ) {
        composable(
            route = Screen.Spacex.route
        ) {
            injectSpacex()
            SpacexScreen(
                viewModel = getViewModel(),
                popBackStack = { navController.popBackStack() },
            )
        }
    }
}
