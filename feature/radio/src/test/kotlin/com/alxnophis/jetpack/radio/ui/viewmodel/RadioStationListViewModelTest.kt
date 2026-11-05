package com.alxnophis.jetpack.radio.ui.viewmodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.radio.data.model.RadioError
import com.alxnophis.jetpack.radio.data.model.RadioStation
import com.alxnophis.jetpack.radio.data.model.RadioStationMother
import com.alxnophis.jetpack.radio.data.repository.RadioStationsRepository
import com.alxnophis.jetpack.radio.playback.RadioPlayerSnapshot
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListStatus
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListEvent
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
internal class RadioStationListViewModelTest {
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
    fun `GIVEN use case returns stations WHEN loaded THEN status is Content`() {
        val viewModel = viewModel(FakeStationsRepository(listOf(RadioStationMother.create()).right()))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.status shouldBeEqualTo RadioStationListStatus.Content
    }

    @Test
    fun `GIVEN use case returns empty list WHEN loaded THEN status is Empty`() {
        val viewModel = viewModel(FakeStationsRepository(emptyList<RadioStation>().right()))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.status shouldBeEqualTo RadioStationListStatus.Empty
    }

    @Test
    fun `GIVEN use case returns NetworkUnavailable WHEN loaded THEN status is Error`() {
        val viewModel = viewModel(FakeStationsRepository(RadioError.NetworkUnavailable.left()))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.status shouldBeEqualTo RadioStationListStatus.Error
    }

    @Test
    fun `GIVEN an error state WHEN Retry is handled THEN the list is requested again`() {
        val repository = FakeStationsRepository(RadioError.ServerError.left())
        val viewModel = viewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        repository.result = listOf(RadioStationMother.create()).right()
        viewModel.handleEvent(RadioStationListEvent.Retry)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.status shouldBeEqualTo RadioStationListStatus.Content
    }

    @Test
    fun `GIVEN loaded stations WHEN a station is selected THEN the queue starts at its index`() {
        val stations = listOf(RadioStationMother.create(id = "a"), RadioStationMother.create(id = "b"))
        val player = FakePlayer()
        val viewModel = viewModel(FakeStationsRepository(stations.right()), player)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.handleEvent(RadioStationListEvent.Select(index = 1))

        player.lastQueueStart shouldBeEqualTo 1
        player.lastQueue shouldBeEqualTo stations
    }

    private fun viewModel(
        repository: RadioStationsRepository,
        player: RadioPlayer = FakePlayer(),
    ) = RadioStationListViewModel(repository, player)

    private class FakeStationsRepository(
        var result: Either<RadioError, List<RadioStation>>,
    ) : RadioStationsRepository {
        override suspend fun getSpanishStations() = result
    }

    private class FakePlayer : RadioPlayer {
        override val snapshot: StateFlow<RadioPlayerSnapshot> = MutableStateFlow(RadioPlayerSnapshot())
        var lastQueue: List<RadioStation> = emptyList()
        var lastQueueStart: Int = -1

        override fun connect() = Unit

        override fun playQueue(
            stations: List<RadioStation>,
            startIndex: Int,
        ) {
            lastQueue = stations
            lastQueueStart = startIndex
        }

        override fun play() = Unit

        override fun pause() = Unit

        override fun stop() = Unit

        override fun next() = Unit

        override fun previous() = Unit

        override fun retry() = Unit

        override fun setSleepTimer(minutes: Int?) = Unit

        override fun release() = Unit
    }
}
