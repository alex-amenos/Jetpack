package com.alxnophis.jetpack.root.navigation

import android.app.Activity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.alxnophis.jetpack.authentication.ui.navigation.authenticationNavGraph
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.game.ballclicker.ui.navigation.ballClickerNavGraph
import com.alxnophis.jetpack.home.ui.navigation.homeNavGraph
import com.alxnophis.jetpack.location.tracker.ui.navigation.locationTrackerNavGraph
import com.alxnophis.jetpack.myplayground.navigation.myPlaygroundNavGraph
import com.alxnophis.jetpack.notifications.navigation.notificationsNavGraph
import com.alxnophis.jetpack.posts.ui.navigation.postsNavGraph
import com.alxnophis.jetpack.router.screen.HOME_ROUTE
import com.alxnophis.jetpack.router.screen.ROOT_ROUTE
import com.alxnophis.jetpack.settings.ui.navigation.settingsNavGraph
import com.alxnophis.jetpack.spacex.ui.navigation.spacexNavGraph

@Composable
fun SetupNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = HOME_ROUTE
) {
    AppTheme {
        setStatusBarColor()
        NavHost(
            navController = navController,
            startDestination = startDestination,
            route = ROOT_ROUTE
        ) {
            homeNavGraph(navController)
            authenticationNavGraph(navController)
            ballClickerNavGraph(navController)
            myPlaygroundNavGraph(navController)
            notificationsNavGraph(navController)
            locationTrackerNavGraph(navController)
            postsNavGraph(navController)
            settingsNavGraph(navController)
            spacexNavGraph(navController)
        }
    }
}

@Composable
internal fun setStatusBarColor(){
    val activity = LocalView.current.context as Activity
    val backgroundArgb = MaterialTheme.colors.primaryVariant.toArgb()
    activity.window.statusBarColor = backgroundArgb
}
