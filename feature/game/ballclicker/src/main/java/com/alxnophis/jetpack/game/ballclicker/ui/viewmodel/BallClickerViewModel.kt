package com.alxnophis.jetpack.game.ballclicker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEffect
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerState
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class BallClickerViewModel(
    initialState: BallClickerState,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<BallClickerEvent, BallClickerState, BallClickerEffect>(initialState) {

    private val timer = ((DEFAULT_TIME_IN_SECONDS - 1) downTo ZERO_INT)
        .asSequence()
        .asFlow()
        .onEach {
            coroutineContext.ensureActive()
            delay(1000L)
            it
        }
        .cancellable()
        .flowOn(dispatcherProvider.default())

    override fun handleEvent(event: BallClickerEvent) {
        when (event) {
            BallClickerEvent.NavigateBack -> {
                stopGame()
                setEffect { BallClickerEffect.NavigateBack }
            }
            BallClickerEvent.BallClicked -> ballClicked()
            BallClickerEvent.Start -> startGame()
            BallClickerEvent.Stop -> stopGame()
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
        timer.collect { seconds ->
            if (!currentState.isTimerRunning) {
                cancel()
            }
            when {
                seconds == ZERO_INT -> setState {
                    copy(
                        currentTimeInSeconds = seconds,
                        isTimerRunning = false,
                    )
                }
                seconds > ZERO_INT && currentState.isTimerRunning -> setState {
                    copy(currentTimeInSeconds = seconds)
                }
                else -> doNothing()
            }
        }
    }

    private fun stopGame() {
        setState {
            copy(
                isTimerRunning = false,
                currentTimeInSeconds = DEFAULT_TIME_IN_SECONDS
            )
        }
    }

    companion object {
        private const val DEFAULT_TIME_IN_SECONDS = 30
        private const val DEFAULT_POINTS = 0
    }
}
