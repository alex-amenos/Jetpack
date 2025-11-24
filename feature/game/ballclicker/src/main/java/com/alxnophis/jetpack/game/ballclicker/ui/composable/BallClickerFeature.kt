package com.alxnophis.jetpack.game.ballclicker.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.viewmodel.BallClickerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BallClickerFeature(onBack: () -> Unit) {
    val viewModel = koinViewModel<BallClickerViewModel>()
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
