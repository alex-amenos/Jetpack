package com.alxnophis.jetpack.root.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alxnophis.jetpack.authentication.ui.navigation.authenticationNavGraph
import com.alxnophis.jetpack.game.ballclicker.ui.navigation.ballClickerNavGraph
import com.alxnophis.jetpack.home.ui.navigation.homeNavGraph
import com.alxnophis.jetpack.location.tracker.ui.navigation.locationTrackerNavGraph
import com.alxnophis.jetpack.posts.ui.navigation.postsNavGraph
import com.alxnophis.jetpack.router.screen.HOME_ROUTE
import com.alxnophis.jetpack.router.screen.ROOT_ROUTE
import com.alxnophis.jetpack.settings.ui.navigation.settingsNavGraph
import com.alxnophis.jetpack.spacex.ui.navigation.spacexNavGraph

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        route = ROOT_ROUTE
    ) {
        homeNavGraph(navController)
        authenticationNavGraph(navController)
        locationTrackerNavGraph(navController)
        postsNavGraph(navController)
        settingsNavGraph(navController)
        ballClickerNavGraph(navController)
        spacexNavGraph(navController)
    }
}
