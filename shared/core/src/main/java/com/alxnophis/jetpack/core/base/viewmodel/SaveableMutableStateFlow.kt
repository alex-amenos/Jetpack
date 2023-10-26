package com.alxnophis.jetpack.core.base.viewmodel

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/***
 * Usage of SaveableMutableStateFlow:
 *
 * class Thing(
 *     private val savedStateHandle: SavedStateHandle,
 *     private val key: String,
 * ) {
 *     private val someExistingStateFlow = MutableStateFlow<String>(savedStateHandle[key] ?: "default Initial")
 *     private val myStateFlow = someExistingStateFlow.saveWith(savedStateHandle = savedStateHandle, key = key)
 *     private val myNewStateFlow = SaveableMutableStateFlow<String>(savedStateHandle = savedStateHandle, key = key)
 *
 * }
 */

fun <T> saveableMutableStateFlow(savedStateHandle: SavedStateHandle, key: String): MutableStateFlow<T> {
    val defaultValue: T = requireNotNull(savedStateHandle[key])
    val wrappedMutableStateFlow = MutableStateFlow<T>(defaultValue)
    return wrappedMutableStateFlow.saveWith(savedStateHandle = savedStateHandle, key = key)
}

fun <T> MutableStateFlow<T>.saveWith(savedStateHandle: SavedStateHandle, key: String): MutableStateFlow<T> {
    savedStateHandle[key] = this.value // (re)save the initial condition if it wasn't there already
    return SaveableMutableStateFlow(savedStateHandle = savedStateHandle, key = key, wrappedMutableStateFlow = this)
}

internal class SaveableMutableStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    private val wrappedMutableStateFlow: MutableStateFlow<T>
) : MutableStateFlow<T> {

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        wrappedMutableStateFlow.collect(collector)
    }

    override val subscriptionCount: StateFlow<Int>
        get() = wrappedMutableStateFlow.subscriptionCount

    override suspend fun emit(value: T) {
        wrappedMutableStateFlow.emit(value)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        wrappedMutableStateFlow.replayCache
    }

    override fun tryEmit(value: T): Boolean =
        wrappedMutableStateFlow.tryEmit(value)

    override var value: T
        get() = wrappedMutableStateFlow.value
        set(value) {
            savedStateHandle[key] = value
            wrappedMutableStateFlow.value = value
        }

    override fun compareAndSet(expect: T, update: T): Boolean =
        wrappedMutableStateFlow.compareAndSet(expect = expect, update = update)

    override val replayCache: List<T>
        get() = wrappedMutableStateFlow.replayCache
}
