package com.alxnophis.jetpack.radio.ui.viewmodel

import android.os.SystemClock
import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.ui.contract.RadioPlayerEvent
import com.alxnophis.jetpack.radio.ui.contract.RadioPlayerUiState
import com.alxnophis.jetpack.radio.ui.contract.artworkUri
import com.alxnophis.jetpack.radio.ui.contract.canNext
import com.alxnophis.jetpack.radio.ui.contract.canPrevious
import com.alxnophis.jetpack.radio.ui.contract.sleepTimerRemainingMillis
import com.alxnophis.jetpack.radio.ui.contract.stationName
import com.alxnophis.jetpack.radio.ui.contract.status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

internal class RadioPlayerViewModel(
    private val player: RadioPlayer,
    initialUiState: RadioPlayerUiState = RadioPlayerUiState.initialState,
    private val clock: () -> Long = { SystemClock.elapsedRealtime() },
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
                    }
                }
            }
        }
        viewModelScope.launch {
            player.snapshot
                .map { it.sleepTimerDeadlineMillis }
                .distinctUntilChanged()
                .catch { throwable -> Timber.e(throwable, "Error in sleep timer countdown") }
                .collectLatest { deadline -> tickSleepTimer(deadline) }
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

    private suspend fun tickSleepTimer(deadline: Long?) {
        if (deadline != null) {
            var remaining = deadline - clock()
            while (remaining > ZERO_MILLIS) {
                updateSleepTimerRemaining(remaining)
                delay(millisUntilDisplayChange(remaining))
                remaining = deadline - clock()
            }
        }
        updateSleepTimerRemaining(null)
    }

    private fun millisUntilDisplayChange(remainingMillis: Long): Long =
        if (remainingMillis < MINUTE_MILLIS) remainingMillis else remainingMillis % MINUTE_MILLIS + 1

    private fun updateSleepTimerRemaining(remainingMillis: Long?) {
        updateUiState {
            copy {
                RadioPlayerUiState.sleepTimerRemainingMillis set remainingMillis
            }
        }
    }

    private companion object {
        const val ZERO_MILLIS = 0L
        const val MINUTE_MILLIS = 60_000L
    }
}
