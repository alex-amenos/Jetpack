package com.alxnophis.jetpack.game.ballclicker.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.game.ballclicker.di.injectBallClicker
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.viewmodel.BallClickerViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun BallClickerFeature(onBack: () -> Unit) {
    injectBallClicker()
    val viewModel = getViewModel<BallClickerViewModel>()
    BallClickerScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                BallClickerEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
