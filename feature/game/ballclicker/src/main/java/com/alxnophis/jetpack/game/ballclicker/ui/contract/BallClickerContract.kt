package com.alxnophis.jetpack.game.ballclicker.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState

internal sealed class BallClickerEvent : UiEvent {
    data object BallClicked : BallClickerEvent()

    data object StartRequested : BallClickerEvent()

    data object StopRequested : BallClickerEvent()

    data object GoBackRequested : BallClickerEvent()
}

@optics
@Immutable
internal data class BallClickerState(
    val currentTimeInSeconds: Int,
    val isTimerRunning: Boolean,
    val points: Int,
) : UiState {
    internal companion object {
        val initialState =
            BallClickerState(
                currentTimeInSeconds = DEFAULT_TIME_IN_SECONDS,
                isTimerRunning = false,
                points = DEFAULT_POINTS,
            )
    }
}

internal const val DEFAULT_TIME_IN_SECONDS = 30
internal const val DEFAULT_POINTS = 0
