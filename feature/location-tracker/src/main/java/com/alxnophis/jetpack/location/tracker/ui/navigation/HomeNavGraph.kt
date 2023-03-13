package com.alxnophis.jetpack.location.tracker.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.location.tracker.di.injectLocationTracker
import com.alxnophis.jetpack.location.tracker.ui.view.LocationTrackerScreen
import com.alxnophis.jetpack.router.screen.LOCATION_TRACKER_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.locationTrackerNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.LocationTracker.route,
        route = LOCATION_TRACKER_ROUTE
    ) {
        composable(
            route = Screen.LocationTracker.route
        ) {
            injectLocationTracker()
            LocationTrackerScreen(
                viewModel = getViewModel(),
                popBackStack = { navController.popBackStack() }
            )
        }
    }
}
