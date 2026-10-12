package com.alxnophis.jetpack.game.ballclicker.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration.Companion.seconds

internal class BallClickerTimerUseCase {
    context(dispatcher: CoroutineDispatcher)
    operator fun invoke(
        start: Long,
        end: Long = 0L,
    ): Flow<Long> =
        flow {
            for (i in start downTo end) {
                emit(i)
                if (i != end) delay(1.seconds)
            }
        }.flowOn(dispatcher)
}
