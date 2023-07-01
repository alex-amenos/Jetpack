package com.alxnophis.jetpack.spacex.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.router.screen.SPACEX_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import com.alxnophis.jetpack.spacex.di.injectSpacex
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesEvent
import com.alxnophis.jetpack.spacex.ui.view.SpacexScreen
import com.alxnophis.jetpack.spacex.ui.viewmodel.LaunchesViewModel
import org.koin.androidx.compose.getViewModel

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
}
