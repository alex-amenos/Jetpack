package com.alxnophis.jetpack.core.base.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    protected fun setState(reduce: State.() -> State) {
        _uiState.update {
            currentState
                .reduce()
                .also { Timber.d("## Set new state: $it") }
        }
    }
}
