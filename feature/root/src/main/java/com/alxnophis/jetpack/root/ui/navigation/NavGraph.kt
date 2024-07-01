package com.alxnophis.jetpack.root.ui.navigation

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alxnophis.jetpack.authentication.ui.navigation.authenticationNavGraph
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.filedownloader.ui.navigation.fileDownloaderNavGraph
import com.alxnophis.jetpack.game.ballclicker.ui.navigation.ballClickerNavGraph
import com.alxnophis.jetpack.home.ui.navigation.homeNavGraph
import com.alxnophis.jetpack.location.tracker.ui.navigation.locationTrackerNavGraph
import com.alxnophis.jetpack.myplayground.navigation.myPlaygroundNavGraph
import com.alxnophis.jetpack.notifications.navigation.notificationsNavGraph
import com.alxnophis.jetpack.posts.ui.navigation.postsNavGraph
import com.alxnophis.jetpack.router.screen.Route
import com.alxnophis.jetpack.settings.ui.navigation.settingsNavGraph
import com.alxnophis.jetpack.spacex.ui.navigation.spacexNavGraph

@Composable
fun SetupNavGraph(navHostController: NavHostController) {
    AppTheme {
        SetStatusBarColor()
        NavHost(
            navController = navHostController,
            startDestination = Route.Home
        ) {
            homeNavGraph(navHostController)
            authenticationNavGraph(navHostController)
            ballClickerNavGraph(navHostController)
            fileDownloaderNavGraph(navHostController)
            myPlaygroundNavGraph(navHostController)
            notificationsNavGraph(navHostController)
            locationTrackerNavGraph(navHostController)
            postsNavGraph(navHostController)
            settingsNavGraph(navHostController)
            spacexNavGraph(navHostController)
        }
    }
}

@Composable
internal fun SetStatusBarColor() {
    val activity = LocalView.current.context as Activity
    val colorArgb = MaterialTheme.colorScheme.primary.toArgb()
    activity.window.statusBarColor = colorArgb
}
