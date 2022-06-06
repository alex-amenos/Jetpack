@file:Suppress("unused")

package com.alxnophis.jetpack.core.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * @see "A safer way to collect flows from Android UIs"
 * https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda
 */
fun AppCompatActivity.repeatOnLifecycle(
    lifecycleState: Lifecycle.State,
    block: (suspend () -> Unit)? = null,
) {
    lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            block?.let { it() }
        }
    }
}

fun AppCompatActivity.repeatOnLifecycleStarted(
    block: (suspend () -> Unit)? = null,
) {
    this.repeatOnLifecycle(Lifecycle.State.STARTED, block)
}

fun AppCompatActivity.repeatOnLifecycleResumed(
    block: (suspend () -> Unit)? = null,
) {
    this.repeatOnLifecycle(Lifecycle.State.RESUMED, block)
}
