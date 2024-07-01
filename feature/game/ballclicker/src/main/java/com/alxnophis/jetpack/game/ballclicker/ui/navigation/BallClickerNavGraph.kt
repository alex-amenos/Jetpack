package com.alxnophis.jetpack.game.ballclicker.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.game.ballclicker.di.injectBallClicker
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.view.BallClickerScreen
import com.alxnophis.jetpack.game.ballclicker.ui.viewmodel.BallClickerViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.ballClickerNavGraph(navController: NavController) {
    composable<Route.GameBallClicker> {
        injectBallClicker()
        val viewModel = getViewModel<BallClickerViewModel>()
        BallClickerScreen(
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            onEvent = { event ->
                when (event) {
                    BallClickerEvent.GoBackRequested -> navController.popBackStack()
                    else -> viewModel.handleEvent(event)
                }
            }
        )
    }
}
