package com.alxnophis.jetpack.radio.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.ui.contract.RadioPlayerUiState
import com.alxnophis.jetpack.radio.ui.contract.RadioPlayerEvent
import com.alxnophis.jetpack.radio.ui.contract.artworkUri
import com.alxnophis.jetpack.radio.ui.contract.canNext
import com.alxnophis.jetpack.radio.ui.contract.canPrevious
import com.alxnophis.jetpack.radio.ui.contract.sleepTimerRemainingMillis
import com.alxnophis.jetpack.radio.ui.contract.stationName
import com.alxnophis.jetpack.radio.ui.contract.status
import kotlinx.coroutines.launch

internal class RadioPlayerViewModel(
    private val player: RadioPlayer,
    initialUiState: RadioPlayerUiState = RadioPlayerUiState.initialState,
) : BaseViewModel<RadioPlayerEvent, RadioPlayerUiState>(initialUiState) {
    init {
        player.connect()
        viewModelScope.launch {
            player.snapshot.collect { snapshot ->
                updateUiState {
                    copy {
                        RadioPlayerUiState.stationName set snapshot.stationName
                        RadioPlayerUiState.artworkUri set snapshot.artworkUri?.toString()
                        RadioPlayerUiState.status set snapshot.status
                        RadioPlayerUiState.canPrevious set snapshot.hasPrevious
                        RadioPlayerUiState.canNext set snapshot.hasNext
                        RadioPlayerUiState.sleepTimerRemainingMillis set snapshot.sleepTimerRemainingMillis
                    }
                }
            }
        }
    }

    override fun handleEvent(event: RadioPlayerEvent) {
        when (event) {
            RadioPlayerEvent.Play -> player.play()
            RadioPlayerEvent.Pause -> player.pause()
            RadioPlayerEvent.Stop -> player.stop()
            RadioPlayerEvent.Next -> player.next()
            RadioPlayerEvent.Previous -> player.previous()
            RadioPlayerEvent.Retry -> player.retry()
            is RadioPlayerEvent.SetSleepTimer -> player.setSleepTimer(event.minutes)
        }
    }
}
