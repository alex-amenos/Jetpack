package com.alxnophis.jetpack.myplayground.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.myplayground.di.injectMyPlayground
import com.alxnophis.jetpack.myplayground.ui.view.MyPlaygroundScreen
import com.alxnophis.jetpack.router.screen.MY_PLAYGROUND_ROUTE
import com.alxnophis.jetpack.router.screen.Screen

fun NavGraphBuilder.myPlaygroundNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.MyPlayground.route,
        route = MY_PLAYGROUND_ROUTE
    ) {
        composable(
            route = Screen.MyPlayground.route
        ) {
            injectMyPlayground()
            MyPlaygroundScreen(
                navController = navController
            )
        }
    }
}
