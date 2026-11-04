package com.alxnophis.jetpack.radio.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.ui.contract.PlayerUiState
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
    initialUiState: PlayerUiState = PlayerUiState.initialState,
) : BaseViewModel<RadioPlayerEvent, PlayerUiState>(initialUiState) {
    init {
        player.connect()
        viewModelScope.launch {
            player.snapshot.collect { snapshot ->
                updateUiState {
                    copy {
                        PlayerUiState.stationName set snapshot.stationName
                        PlayerUiState.artworkUri set snapshot.artworkUri?.toString()
                        PlayerUiState.status set snapshot.status
                        PlayerUiState.canPrevious set snapshot.hasPrevious
                        PlayerUiState.canNext set snapshot.hasNext
                        PlayerUiState.sleepTimerRemainingMillis set snapshot.sleepTimerRemainingMillis
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
