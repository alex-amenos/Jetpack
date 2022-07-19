package com.alxnophis.jetpack.spacex.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.router.screen.SPACEX_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import com.alxnophis.jetpack.spacex.ui.view.SpacexScreen

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
            SpacexScreen(navController = navController)
        }
    }
}
