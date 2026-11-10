package com.alxnophis.jetpack.radio.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.radio.playback.RadioPlaybackStatus

internal sealed interface RadioPlayerEvent : UiEvent {
    data object Play : RadioPlayerEvent

    data object Pause : RadioPlayerEvent

    data object Stop : RadioPlayerEvent

    data object Next : RadioPlayerEvent

    data object Previous : RadioPlayerEvent

    data object Retry : RadioPlayerEvent

    data class SetSleepTimer(
        val minutes: Int?,
    ) : RadioPlayerEvent
}

@optics
@Immutable
internal data class RadioPlayerUiState(
    val stationName: String?,
    val artworkUri: String?,
    val status: RadioPlaybackStatus,
    val canPrevious: Boolean,
    val canNext: Boolean,
    val sleepTimerRemainingMillis: Long?,
) : UiState {
    internal companion object {
        val initialState =
            RadioPlayerUiState(
                stationName = null,
                artworkUri = null,
                status = RadioPlaybackStatus.Idle,
                canPrevious = false,
                canNext = false,
                sleepTimerRemainingMillis = null,
            )
    }
}
