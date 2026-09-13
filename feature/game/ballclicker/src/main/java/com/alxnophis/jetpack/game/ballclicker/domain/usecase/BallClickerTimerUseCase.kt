package com.alxnophis.jetpack.game.ballclicker.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class BallClickerTimerUseCase(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    operator fun invoke(
        start: Long,
        end: Long = 0L,
    ): Flow<Long> =
        flow {
            for (i in start downTo end) {
                emit(i)
                if (i != end) delay(1_000)
            }
        }.flowOn(defaultDispatcher)
}
