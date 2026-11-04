package com.alxnophis.jetpack.radio.ui.viewmodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.Station
import com.alxnophis.jetpack.radio.domain.model.StationMother
import com.alxnophis.jetpack.radio.domain.repository.StationsRepository
import com.alxnophis.jetpack.radio.domain.usecase.GetSpanishStationsUseCase
import com.alxnophis.jetpack.radio.playback.PlayerSnapshot
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.ui.contract.ListStatus
import com.alxnophis.jetpack.radio.ui.contract.StationListEvent
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
internal class StationListViewModelTest {
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
        val viewModel = viewModel(FakeStationsRepository(listOf(StationMother.create()).right()))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.status shouldBeEqualTo ListStatus.Content
    }

    @Test
    fun `GIVEN use case returns empty list WHEN loaded THEN status is Empty`() {
        val viewModel = viewModel(FakeStationsRepository(emptyList<Station>().right()))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.status shouldBeEqualTo ListStatus.Empty
    }

    @Test
    fun `GIVEN use case returns NetworkUnavailable WHEN loaded THEN status is Error`() {
        val viewModel = viewModel(FakeStationsRepository(RadioError.NetworkUnavailable.left()))

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.status shouldBeEqualTo ListStatus.Error
    }

    @Test
    fun `GIVEN an error state WHEN Retry is handled THEN the list is requested again`() {
        val repository = FakeStationsRepository(RadioError.ServerError.left())
        val viewModel = viewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        repository.result = listOf(StationMother.create()).right()
        viewModel.handleEvent(StationListEvent.Retry)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.currentUiState.status shouldBeEqualTo ListStatus.Content
    }

    @Test
    fun `GIVEN loaded stations WHEN a station is selected THEN the queue starts at its index`() {
        val stations = listOf(StationMother.create(id = "a"), StationMother.create(id = "b"))
        val player = FakePlayer()
        val viewModel = viewModel(FakeStationsRepository(stations.right()), player)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.handleEvent(StationListEvent.Select(index = 1))

        player.lastQueueStart shouldBeEqualTo 1
        player.lastQueue shouldBeEqualTo stations
    }

    private fun viewModel(
        repository: StationsRepository,
        player: RadioPlayer = FakePlayer(),
    ) = StationListViewModel(GetSpanishStationsUseCase(repository), player)

    private class FakeStationsRepository(
        var result: Either<RadioError, List<Station>>,
    ) : StationsRepository {
        override suspend fun getSpanishStations() = result
    }

    private class FakePlayer : RadioPlayer {
        override val snapshot: StateFlow<PlayerSnapshot> = MutableStateFlow(PlayerSnapshot())
        var lastQueue: List<Station> = emptyList()
        var lastQueueStart: Int = -1

        override fun connect() = Unit

        override fun playQueue(
            stations: List<Station>,
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
