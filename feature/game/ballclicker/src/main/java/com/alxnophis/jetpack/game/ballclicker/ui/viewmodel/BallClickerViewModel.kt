package com.alxnophis.jetpack.game.ballclicker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerState
import com.alxnophis.jetpack.game.ballclicker.ui.contract.DEFAULT_POINTS
import com.alxnophis.jetpack.game.ballclicker.ui.contract.DEFAULT_TIME_IN_SECONDS
import com.alxnophis.jetpack.game.ballclicker.ui.contract.currentTimeInSeconds
import com.alxnophis.jetpack.game.ballclicker.ui.contract.isTimerRunning
import com.alxnophis.jetpack.game.ballclicker.ui.contract.points
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class BallClickerViewModel(
    initialState: BallClickerState = BallClickerState.initialState,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : BaseViewModel<BallClickerEvent, BallClickerState>(initialState) {
    private val timerScope = CoroutineScope(defaultDispatcher + SupervisorJob())

    private var timerJob: Job? = null

    override fun handleEvent(event: BallClickerEvent) {
        viewModelScope.launch {
            when (event) {
                BallClickerEvent.BallClicked -> ballClicked()
                BallClickerEvent.StartRequested -> startGame()
                BallClickerEvent.StopRequested -> stopGame()
                BallClickerEvent.GoBackRequested -> throw IllegalStateException("GoBackRequested is not implemented")
            }
        }
    }

    private fun ballClicked() {
        _uiState.updateCopy {
            BallClickerState.points set (currentState.points + 1)
        }
    }

    private fun startGame() =
        viewModelScope.launch {
            _uiState.updateCopy {
                BallClickerState.isTimerRunning set true
                BallClickerState.points set DEFAULT_POINTS
            }
            tickerFlow()
                .onEach { seconds ->
                    _uiState.updateCopy {
                        BallClickerState.currentTimeInSeconds set seconds.toInt()
                    }
                }.onCompletion {
                    _uiState.updateCopy {
                        BallClickerState.currentTimeInSeconds set DEFAULT_TIME_IN_SECONDS
                        BallClickerState.isTimerRunning set false
                    }
                }.cancellable()
                .launchIn(timerScope)
                .also { job -> timerJob = job }
        }

    private fun stopGame() =
        viewModelScope.launch {
            timerJob?.cancel()
            _uiState.updateCopy {
                BallClickerState.isTimerRunning set false
                BallClickerState.currentTimeInSeconds set DEFAULT_TIME_IN_SECONDS
            }
        }

    private fun tickerFlow(
        start: Long = DEFAULT_TIME_IN_SECONDS.toLong(),
        end: Long = 0L,
    ): Flow<Long> =
        flow {
            for (i in start downTo end) {
                emit(i)
                delay(1_000)
            }
        }
}
