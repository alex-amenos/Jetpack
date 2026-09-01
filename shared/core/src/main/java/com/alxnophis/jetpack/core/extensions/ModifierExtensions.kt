package com.alxnophis.jetpack.core.extensions

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

/**
 * Conditionally enables [testTagsAsResourceId] semantics for UI testing (e.g., Maestro, UIAutomator).
 * Only applied when [enabled] is true (defaults to debug builds via [isDebugBuildType])
 * to avoid overhead and exclude test metadata from production release builds.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.testTagsAsResourceIdInDebug(
    enabled: Boolean = isDebugBuildType(),
): Modifier =
    if (enabled) {
        this.semantics {
            testTagsAsResourceId = true
        }
    } else {
        this
    }
