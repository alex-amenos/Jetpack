package com.alxnophis.jetpack.game.ballclicker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerState
import com.alxnophis.jetpack.game.ballclicker.ui.contract.DEFAULT_POINTS
import com.alxnophis.jetpack.game.ballclicker.ui.contract.DEFAULT_TIME_IN_SECONDS
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
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
    initialState: BallClickerState,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<BallClickerEvent, BallClickerState>(initialState) {

    private val timerScope = CoroutineScope(dispatcherProvider.default() + SupervisorJob())

    private var timerJob: Job? = null

    override fun handleEvent(event: BallClickerEvent) {
        viewModelScope.launch {
            when (event) {
                BallClickerEvent.BallClicked -> ballClicked()
                BallClickerEvent.Start -> startGame()
                BallClickerEvent.Stop -> stopGame()
            }
        }
    }

    private fun ballClicked() {
        setState {
            copy(points = points + 1)
        }
    }

    private fun startGame() = viewModelScope.launch {
        setState {
            copy(
                isTimerRunning = true,
                points = DEFAULT_POINTS
            )
        }
        tickerFlow()
            .onEach { seconds ->
                setState {
                    copy(currentTimeInSeconds = seconds.toInt())
                }
            }
            .onCompletion {
                setState {
                    copy(
                        currentTimeInSeconds = DEFAULT_TIME_IN_SECONDS,
                        isTimerRunning = false,
                    )
                }
            }
            .cancellable()
            .launchIn(timerScope)
            .also { job -> timerJob = job }
    }

    private fun stopGame() = viewModelScope.launch {
        timerJob?.cancel()
        setState {
            copy(
                isTimerRunning = false,
                currentTimeInSeconds = DEFAULT_TIME_IN_SECONDS
            )
        }
    }

    private fun tickerFlow(start: Long = DEFAULT_TIME_IN_SECONDS.toLong(), end: Long = 0L): Flow<Long> =
        flow {
            for (i in start downTo end) {
                emit(i)
                delay(1_000)
            }
        }
}
