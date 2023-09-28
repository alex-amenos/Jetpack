package com.alxnophis.jetpack.home.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.home.di.injectHome
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.view.HomeScreen
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import com.alxnophis.jetpack.router.screen.HOME_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    finish: () -> Unit
) {
    navigation(
        startDestination = Screen.Home.route,
        route = HOME_ROUTE
    ) {
        composable(
            route = Screen.Home.route
        ) {
            injectHome()
            val viewModel = getViewModel<HomeViewModel>()
            val state: HomeState = viewModel.uiState.collectAsStateWithLifecycle().value
            HomeScreen(
                state = state,
                onEvent = { event ->
                    when (event) {
                        HomeEvent.GoBackRequested -> {
                            navController.popBackStack()
                            finish()
                        }
                        is HomeEvent.NavigationRequested -> navController.navigate(event.route)
                        else -> viewModel.handleEvent(event)
                    }
                }
            )
        }
    }
}
