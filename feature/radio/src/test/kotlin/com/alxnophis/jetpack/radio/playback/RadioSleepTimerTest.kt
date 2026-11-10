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
            val timer =
                RadioSleepTimer(
                    scope = this,
                    clock = { testScheduler.currentTime },
                ) { expiredCount++ }

            timer.start(minutes = 1)
            advanceTimeBy(60_001L.milliseconds)

            expiredCount shouldBeEqualTo 1
            timer.remainingMillis.value.shouldBeNull()
        }

    @Test
    fun `GIVEN an active timer WHEN cancelled THEN onExpired is never called and remaining is null`() =
        runTest {
            var expiredCount = 0
            val timer =
                RadioSleepTimer(
                    scope = this,
                    clock = { testScheduler.currentTime },
                ) { expiredCount++ }

            timer.start(minutes = 1)
            advanceTimeBy(30_000L.milliseconds)
            timer.cancel()
            advanceTimeBy(60_000L.milliseconds)

            expiredCount shouldBeEqualTo 0
            timer.remainingMillis.value.shouldBeNull()
        }

    @Test
    fun `GIVEN an active timer WHEN clock advances with drift THEN remainingMillis reflects actual elapsed time`() =
        runTest {
            var currentTime = 10_000L
            val timer =
                RadioSleepTimer(
                    scope = this,
                    clock = { currentTime },
                ) {}

            timer.start(minutes = 1)
            testScheduler.runCurrent()
            currentTime += 15_000L
            advanceTimeBy(1_001L.milliseconds)

            timer.remainingMillis.value shouldBeEqualTo 45_000L
            timer.cancel()
        }

    @Test
    fun `GIVEN a 1 minute timer WHEN device sleeps past deadline THEN onExpired is called immediately on resumption`() =
        runTest {
            var currentTime = 10_000L
            var expiredCount = 0
            val timer =
                RadioSleepTimer(
                    scope = this,
                    clock = { currentTime },
                ) { expiredCount++ }

            timer.start(minutes = 1)
            testScheduler.runCurrent()
            currentTime += 120_000L
            advanceTimeBy(1_001L.milliseconds)

            expiredCount shouldBeEqualTo 1
            timer.remainingMillis.value.shouldBeNull()
        }
}
