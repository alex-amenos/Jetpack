package com.alxnophis.jetpack.game.ballclicker.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class BallClickerEvent : UiEvent {
    object BallClicked : BallClickerEvent()
    object Start : BallClickerEvent()
    object Stop : BallClickerEvent()
}

internal data class BallClickerState(
    val currentTimeInSeconds: Int = 30,
    val isTimerRunning: Boolean = false,
    val points: Int = 0,
) : UiState
