package com.alxnophis.jetpack.core.base.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseViewModel<Event : UiEvent, State : UiState>(
    initialState: State
) : ViewModel() {

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    /**
     * Handle each Event
     */
    abstract fun handleEvent(event: Event)

    /**
     * Set new State
     */
    protected fun updateState(reduce: State.() -> State) {
        _uiState
            .update { it.reduce() }
            .also { Timber.d("## Set new state: $currentState") }
    }

    /**
     * Update new State and get prior State
     */
    protected fun getAndUpdate(reduce: State.() -> State): State =
        _uiState
            .getAndUpdate { it.reduce() }
            .also { Timber.d("## Set new state: $currentState") }

    /**
     * Update and get new State
     */
    protected fun updateAndGet(reduce: State.() -> State): State =
        _uiState
            .updateAndGet { it.reduce() }
            .also { newState -> Timber.d("## Set new state: $newState") }
}
