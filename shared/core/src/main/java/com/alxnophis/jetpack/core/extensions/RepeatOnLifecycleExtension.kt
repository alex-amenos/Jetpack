@file:Suppress("unused")

package com.alxnophis.jetpack.core.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
        lifecycle.repeatOnLifecycle(lifecycleState) {
            launch {
                block?.let { it() }
            }
        }
    }
}

fun AppCompatActivity.repeatOnLifecycleStarted(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (suspend () -> Unit)? = null,
) {
    this.repeatOnLifecycle(lifecycleState, block)
}

fun AppCompatActivity.repeatOnLifecycleResumed(
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: (suspend () -> Unit)? = null,
) {
    this.repeatOnLifecycle(lifecycleState, block)
}
