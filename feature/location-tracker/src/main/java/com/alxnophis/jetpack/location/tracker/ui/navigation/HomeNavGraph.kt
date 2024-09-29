package com.alxnophis.jetpack.location.tracker.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.location.tracker.di.injectLocationTracker
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.view.LocationTrackerScreen
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.locationTrackerNavGraph(navController: NavHostController) {
    composable<Route.LocationTracker> {
        injectLocationTracker()
        val viewModel = getViewModel<LocationTrackerViewModel>()
        LocationTrackerScreen(
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            onEvent = { event ->
                when (event) {
                    is LocationTrackerEvent.GoBackRequested -> navController.popBackStack()
                    else -> viewModel.handleEvent(event)
                }
            },
        )
    }
}
