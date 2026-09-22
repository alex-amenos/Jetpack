package com.alxnophis.jetpack.game.ballclicker.ui.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.game.ballclicker.domain.usecase.BallClickerTimerUseCase
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerState
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BallClickerViewModelTest : BaseViewModelUnitTest() {
    private val useCase = BallClickerTimerUseCase()
    private lateinit var viewModel: BallClickerViewModel

    override fun beforeEachCompleted() {
        viewModel =
            BallClickerViewModel(
                ballClickerTimerUseCase = useCase,
                initialState = BallClickerState.initialState,
                defaultDispatcher = testDispatcher,
            )
    }

    @Test
    fun `WHEN ball clicked THEN increments points`() =
        runTest {
            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo BallClickerState.initialState

                viewModel.handleEvent(BallClickerEvent.BallClicked)
                awaitItem() shouldBeEqualTo BallClickerState.initialState.copy(points = 1)

                viewModel.handleEvent(BallClickerEvent.BallClicked)
                awaitItem() shouldBeEqualTo BallClickerState.initialState.copy(points = 2)
            }
        }

    @Test
    fun `WHEN stop requested THEN cancels running timer`() =
        runTest {
            viewModel.handleEvent(BallClickerEvent.StopRequested)
            viewModel.currentUiState.isTimerRunning shouldBeEqualTo false
        }
}
