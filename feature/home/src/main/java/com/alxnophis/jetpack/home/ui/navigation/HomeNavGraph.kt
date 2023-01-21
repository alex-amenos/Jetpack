package com.alxnophis.jetpack.home.ui.navigation

import android.app.Activity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.home.di.injectHome
import com.alxnophis.jetpack.home.ui.view.HomeScreen
import com.alxnophis.jetpack.router.screen.HOME_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Home.route,
        route = HOME_ROUTE
    ) {
        composable(
            route = Screen.Home.route,
        ) {
            injectHome()
            HomeScreen(
                viewModel = getViewModel(),
                backOrFinish = { activity: Activity? ->
                    if (!navController.popBackStack()) {
                        activity?.finish()
                    }
                },
                navigateTo = { route -> navController.navigate(route) },
            )
        }
    }
}
