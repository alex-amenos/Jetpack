package com.alxnophis.jetpack.utils

import androidx.compose.ui.test.junit4.ComposeTestRule

/**
 * Extension functions and utilities for E2E testing
 */

/**
 * Waits for a specific amount of time (use sparingly, prefer waitUntil when possible)
 */
fun ComposeTestRule.waitForMillis(millis: Long) {
    Thread.sleep(millis)
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
