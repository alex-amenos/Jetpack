package com.alxnophis.jetpack.musicdashboard.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.musicdashboard.di.injectMusicDashboard
import com.alxnophis.jetpack.musicdashboard.ui.view.MusicDashboardScreen
import com.alxnophis.jetpack.router.screen.MUSIC_DASHBOARD
import com.alxnophis.jetpack.router.screen.Screen

fun NavGraphBuilder.musicDashboardNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.MusicDashboard.route,
        route = MUSIC_DASHBOARD
    ) {
        composable(
            route = Screen.MusicDashboard.route,
        ) {
            injectMusicDashboard()
            MusicDashboardScreen(
                navController = navController,
            )
        }
    }
}
