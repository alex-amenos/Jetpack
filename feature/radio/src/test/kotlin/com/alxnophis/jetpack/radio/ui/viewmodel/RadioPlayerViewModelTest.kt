package com.alxnophis.jetpack.radio.ui.viewmodel

import com.alxnophis.jetpack.radio.domain.model.RadioStation
import com.alxnophis.jetpack.radio.playback.PlaybackStatus
import com.alxnophis.jetpack.radio.playback.PlayerSnapshot
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.ui.contract.RadioPlayerEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RadioPlayerViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN first station WHEN snapshot is collected THEN canPrevious is false`() {
        val player = FakeRadioPlayer(PlayerSnapshot(stationName = "Cadena SER", hasPrevious = false, hasNext = true))

        val viewModel = RadioPlayerViewModel(player)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.canPrevious shouldBeEqualTo false
        viewModel.currentUiState.canNext shouldBeEqualTo true
    }

    @Test
    fun `GIVEN last station WHEN snapshot is collected THEN canNext is false`() {
        val player = FakeRadioPlayer(PlayerSnapshot(stationName = "Cadena SER", hasPrevious = true, hasNext = false))

        val viewModel = RadioPlayerViewModel(player)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.canNext shouldBeEqualTo false
    }

    @Test
    fun `GIVEN error status WHEN Retry is handled THEN player retry is called`() {
        val player = FakeRadioPlayer(PlayerSnapshot(status = PlaybackStatus.Error))
        val viewModel = RadioPlayerViewModel(player)

        viewModel.handleEvent(RadioPlayerEvent.Retry)

        player.retryCount shouldBeEqualTo 1
    }

    @Test
    fun `GIVEN playing WHEN Stop is handled THEN player stop is called`() {
        val player = FakeRadioPlayer(PlayerSnapshot(status = PlaybackStatus.Playing))
        val viewModel = RadioPlayerViewModel(player)

        viewModel.handleEvent(RadioPlayerEvent.Stop)

        player.stopCount shouldBeEqualTo 1
    }

    private class FakeRadioPlayer(
        initial: PlayerSnapshot,
    ) : RadioPlayer {
        private val _snapshot = MutableStateFlow(initial)
        override val snapshot: StateFlow<PlayerSnapshot> = _snapshot
        var retryCount = 0
        var stopCount = 0

        override fun connect() = Unit

        override fun playQueue(
            stations: List<RadioStation>,
            startIndex: Int,
        ) = Unit

        override fun play() = Unit

        override fun pause() = Unit

        override fun stop() {
            stopCount++
        }

        override fun next() = Unit

        override fun previous() = Unit

        override fun retry() {
            retryCount++
        }

        override fun setSleepTimer(minutes: Int?) = Unit

        override fun release() = Unit
    }
}
