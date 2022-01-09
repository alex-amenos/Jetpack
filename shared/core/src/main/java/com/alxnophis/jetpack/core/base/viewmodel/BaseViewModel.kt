package com.alxnophis.jetpack.core.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * MVI Architecture with Kotlin Flows and Channels by Yusuf Ceylan
 * POST: https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d
 * REPOSITORY: https://github.com/yusufceylan/MVI-Playground
 */
abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeEvents()
    }

    /**
     * Start listening to Event
     */
    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    /**
     * Handle each event
     */
    abstract fun handleEvent(event: Event)

    /**
     * Set new Event
     */
    fun setEvent(event: Event) {
        val newEvent = event
        Timber.d("## Set new event: $newEvent")
        viewModelScope.launch { _event.emit(newEvent) }
    }

    /**
     * Set new Ui State
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        Timber.d("## Set new state: $newState")
        viewModelScope.launch { _uiState.value = newState }
    }

    /**
     * Set new Effect
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        Timber.d("## Set a new effect: $effectValue")
        viewModelScope.launch { _effect.send(effectValue) }
    }
}
