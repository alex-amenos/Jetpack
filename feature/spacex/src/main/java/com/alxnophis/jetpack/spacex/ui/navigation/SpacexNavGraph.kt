package com.alxnophis.jetpack.spacex.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.router.screen.Route
import com.alxnophis.jetpack.spacex.di.injectSpacex
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesEvent
import com.alxnophis.jetpack.spacex.ui.view.SpacexScreen
import com.alxnophis.jetpack.spacex.ui.viewmodel.LaunchesViewModel
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.spacexNavGraph(navController: NavController) {
    composable<Route.Spacex> {
        injectSpacex()
        val viewModel = getViewModel<LaunchesViewModel>()
        SpacexScreen(
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            onEvent = { event ->
                when (event) {
                    LaunchesEvent.GoBackRequested -> navController.popBackStack()
                    else -> viewModel.handleEvent(event)
                }
            }
        )
    }
}
