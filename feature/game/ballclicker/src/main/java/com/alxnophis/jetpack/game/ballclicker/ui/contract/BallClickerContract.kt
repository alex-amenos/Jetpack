package com.alxnophis.jetpack.game.ballclicker.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class BallClickerEvent : UiEvent {
    object BallClicked : BallClickerEvent()
    object StartRequested : BallClickerEvent()
    object StopRequested : BallClickerEvent()
    object GoBackRequested : BallClickerEvent()
}

@optics
internal data class BallClickerState(
    val currentTimeInSeconds: Int,
    val isTimerRunning: Boolean,
    val points: Int
) : UiState {
    internal companion object {
        val initialState = BallClickerState(
            currentTimeInSeconds = DEFAULT_TIME_IN_SECONDS,
            isTimerRunning = false,
            points = DEFAULT_POINTS
        )
    }
}

internal const val DEFAULT_TIME_IN_SECONDS = 30
internal const val DEFAULT_POINTS = 0
