package com.alxnophis.jetpack.home.ui.navigation

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.home.di.injectHome
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.view.HomeScreen
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    composable<Route.Home> {
        injectHome()
        val finish = { ActivityCompat.finishAffinity(navController.context as Activity) }
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
