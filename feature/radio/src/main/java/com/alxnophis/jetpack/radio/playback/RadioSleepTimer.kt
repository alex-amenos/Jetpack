package com.alxnophis.jetpack.radio.playback

import android.os.SystemClock
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

internal class RadioSleepTimer(
    private val scope: CoroutineScope,
    private val clock: () -> Long = { SystemClock.elapsedRealtime() },
    private val onExpired: () -> Unit,
) {
    private var job: Job? = null
    private val _deadlineMillis = MutableStateFlow<Long?>(null)
    val deadlineMillis: StateFlow<Long?> = _deadlineMillis.asStateFlow()

    fun start(minutes: Int) {
        job?.cancel()
        job =
            scope.launch {
                val deadline = clock() + minutes * MILLIS_PER_MINUTE
                _deadlineMillis.value = deadline
                var remaining = deadline - clock()
                while (remaining > ZERO_INT) {
                    delay(minOf(TICK_MILLIS, remaining).milliseconds)
                    remaining = deadline - clock()
                }
                _deadlineMillis.value = null
                onExpired()
            }
    }

    fun cancel() {
        job?.cancel()
        job = null
        _deadlineMillis.value = null
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
        const val TICK_MILLIS = 1_000L
    }
}
