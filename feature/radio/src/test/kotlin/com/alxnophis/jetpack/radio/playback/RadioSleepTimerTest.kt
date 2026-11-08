package com.alxnophis.jetpack.radio.playback

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
internal class RadioSleepTimerTest {
    @Test
    fun `GIVEN a 1 minute timer WHEN time elapses THEN onExpired is called once`() =
        runTest {
            var expiredCount = 0
            val timer = RadioSleepTimer(scope = this) { expiredCount++ }

            timer.start(minutes = 1)
            advanceTimeBy(60_001L.milliseconds)

            expiredCount shouldBeEqualTo 1
            timer.remainingMillis.value.shouldBeNull()
        }

    @Test
    fun `GIVEN an active timer WHEN cancelled THEN onExpired is never called and remaining is null`() =
        runTest {
            var expiredCount = 0
            val timer = RadioSleepTimer(scope = this) { expiredCount++ }

            timer.start(minutes = 1)
            advanceTimeBy(30_000L.milliseconds)
            timer.cancel()
            advanceTimeBy(60_000L.milliseconds)

            expiredCount shouldBeEqualTo 0
            timer.remainingMillis.value.shouldBeNull()
        }
}
