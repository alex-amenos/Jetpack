package com.alxnophis.jetpack.game.ballclicker.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class BallClickerEvent : UiEvent {
    object BallClicked : BallClickerEvent()
    object Start : BallClickerEvent()
    object Stop : BallClickerEvent()
}

internal data class BallClickerState(
    val currentTimeInSeconds: Int = DEFAULT_TIME_IN_SECONDS,
    val isTimerRunning: Boolean = false,
    val points: Int = DEFAULT_POINTS
) : UiState

internal const val DEFAULT_TIME_IN_SECONDS = 30
internal const val DEFAULT_POINTS = 0
