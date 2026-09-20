package com.alxnophis.jetpack.game.ballclicker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.game.ballclicker.domain.usecase.BallClickerTimerUseCase
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerState
import com.alxnophis.jetpack.game.ballclicker.ui.contract.DEFAULT_POINTS
import com.alxnophis.jetpack.game.ballclicker.ui.contract.DEFAULT_TIME_IN_SECONDS
import com.alxnophis.jetpack.game.ballclicker.ui.contract.currentTimeInSeconds
import com.alxnophis.jetpack.game.ballclicker.ui.contract.isTimerRunning
import com.alxnophis.jetpack.game.ballclicker.ui.contract.points
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

internal class BallClickerViewModel(
    private val ballClickerTimerUseCase: BallClickerTimerUseCase,
    initialState: BallClickerState = BallClickerState.initialState,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : BaseViewModel<BallClickerEvent, BallClickerState>(initialState) {
    private var timerJob: Job? = null

    override fun handleEvent(event: BallClickerEvent) {
        when (event) {
            BallClickerEvent.BallClicked -> ballClicked()
            BallClickerEvent.StartRequested -> startGame()
            BallClickerEvent.StopRequested -> stopGame()
            BallClickerEvent.GoBackRequested -> throw IllegalStateException("GoBackRequested is not implemented")
        }
    }

    private fun ballClicked() {
        _uiState.updateCopy {
            BallClickerState.points set (currentUiState.points + 1)
        }
    }

    private fun startGame() {
        timerJob?.cancel()
        _uiState.updateCopy {
            BallClickerState.isTimerRunning set true
            BallClickerState.points set DEFAULT_POINTS
        }
        timerJob =
            viewModelScope.launch {
                context(defaultDispatcher) {
                    ballClickerTimerUseCase(DEFAULT_TIME_IN_SECONDS.toLong())
                }.onEach { seconds ->
                    _uiState.updateCopy {
                        BallClickerState.currentTimeInSeconds set seconds.toInt()
                    }
                }.onCompletion {
                    _uiState.updateCopy {
                        BallClickerState.currentTimeInSeconds set DEFAULT_TIME_IN_SECONDS
                        BallClickerState.isTimerRunning set false
                    }
                }.cancellable()
                    .catch { throwable ->
                        Timber.e(throwable, "Error in ball clicker timer")
                    }.collect()
            }
    }

    private fun stopGame() {
        timerJob?.cancel()
        _uiState.updateCopy {
            BallClickerState.isTimerRunning set false
            BallClickerState.currentTimeInSeconds set DEFAULT_TIME_IN_SECONDS
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
