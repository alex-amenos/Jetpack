package com.alxnophis.jetpack.core.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseViewModel<Event : UiEvent, State : UiState>(
    initialState: State
) : ViewModel() {

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val uiEvent = _event.asSharedFlow()

    init {
        subscribeEvents()
    }

    /**
     * Start listening to Event
     */
    private fun subscribeEvents() {
        viewModelScope.launch {
            uiEvent.collect {
                handleEvent(it)
            }
        }
    }

    /**
     * Handle each Event
     */
    abstract fun handleEvent(event: Event)

    /**
     * Set new Event
     */
    fun setEvent(event: Event) {
        Timber.d("## Set new event: $event")
        viewModelScope.launch {
            _event.emit(event)
        }
    }

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
