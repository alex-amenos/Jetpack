package com.alxnophis.jetpack.radio.playback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class SleepTimer(
    private val scope: CoroutineScope,
    private val onExpired: () -> Unit,
) {
    private var job: Job? = null
    private val _remainingMillis = MutableStateFlow<Long?>(null)
    val remainingMillis: StateFlow<Long?> = _remainingMillis.asStateFlow()

    fun start(minutes: Int) {
        job?.cancel()
        job =
            scope.launch {
                var remaining = minutes * MILLIS_PER_MINUTE
                while (remaining > 0) {
                    _remainingMillis.value = remaining
                    delay(TICK_MILLIS)
                    remaining -= TICK_MILLIS
                }
                _remainingMillis.value = null
                onExpired()
            }
    }

    fun cancel() {
        job?.cancel()
        job = null
        _remainingMillis.value = null
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
        const val TICK_MILLIS = 1_000L
    }
}
