package com.alxnophis.jetpack.radio.playback

import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test

internal class SleepTimerTest {
    @Test
    fun `GIVEN a 1 minute timer WHEN time elapses THEN onExpired is called once`() =
        runTest {
            var expiredCount = 0
            val timer = SleepTimer(scope = this) { expiredCount++ }

            timer.start(minutes = 1)
            advanceTimeBy(60_001L)

            expiredCount shouldBeEqualTo 1
            timer.remainingMillis.value.shouldBeNull()
        }

    @Test
    fun `GIVEN an active timer WHEN cancelled THEN onExpired is never called and remaining is null`() =
        runTest {
            var expiredCount = 0
            val timer = SleepTimer(scope = this) { expiredCount++ }

            timer.start(minutes = 1)
            advanceTimeBy(30_000L)
            timer.cancel()
            advanceTimeBy(60_000L)

            expiredCount shouldBeEqualTo 0
            timer.remainingMillis.value.shouldBeNull()
        }
}
