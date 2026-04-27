package com.alxnophis.jetpack.utils

import androidx.compose.ui.test.junit4.ComposeTestRule

/**
 * Advances the Compose test clock by a specific amount of time.
 * Prefer condition-based waiting when possible.
 */
fun ComposeTestRule.waitForMillis(millis: Long) {
    mainClock.advanceTimeBy(millis)
    waitForIdle()
}

/**
 * Waits for idle and then waits for a condition
 */
fun ComposeTestRule.waitForIdleAndCondition(
    timeoutMillis: Long = 5000,
    condition: () -> Boolean,
) {
    waitForIdle()
    waitUntil(timeoutMillis, condition)
}
