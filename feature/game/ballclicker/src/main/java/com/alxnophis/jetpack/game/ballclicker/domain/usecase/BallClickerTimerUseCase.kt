package com.alxnophis.jetpack.game.ballclicker.domain.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class BallClickerTimerUseCase {
    operator fun invoke(
        start: Long,
        end: Long = 0L,
    ): Flow<Long> =
        flow {
            for (i in start downTo end) {
                emit(i)
                if (i != end) delay(1_000)
            }
        }
}
