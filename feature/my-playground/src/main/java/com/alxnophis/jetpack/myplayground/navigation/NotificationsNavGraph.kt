package com.alxnophis.jetpack.myplayground.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.myplayground.di.injectMyPlayground
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundEvent
import com.alxnophis.jetpack.myplayground.ui.view.MyPlaygroundScreen
import com.alxnophis.jetpack.myplayground.ui.viewmodel.MyPlaygroundViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.myPlaygroundNavGraph(navController: NavController) {
    composable<Route.MyPlayground> {
        injectMyPlayground()
        val viewModel = getViewModel<MyPlaygroundViewModel>()
        MyPlaygroundScreen(
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            onEvent = { event ->
                when (event) {
                    MyPlaygroundEvent.GoBackRequested -> navController.popBackStack()
                    else -> viewModel.handleEvent(event)
                }
            },
        )
    }
}
