package com.alxnophis.jetpack.core.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Related articles to create the BaseViewModel
 *
 * 1. MVI Architecture with Kotlin Flows and Channels:
 *    https://medium.com/proandroiddev/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d
 * 2. Atomic Updates on MutableStateFlow:
 *    https://medium.com/geekculture/atomic-updates-with-mutablestateflow-dc0331724405
 * 3. Sending ViewModel Events to the UI in Android:
 *    https://medium.com/proandroiddev/sending-view-model-events-to-the-ui-eef76bdd632c
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val uiEvent = _event.asSharedFlow()

    private val _effect: Channel<Effect> = Channel(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

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

    /**
     * Set new Side Effect
     */
    protected fun setEffect(builder: () -> Effect) {
        val newEffect = builder()
        Timber.d("## Set new side Effect: $newEffect")
        viewModelScope.launch {
            _effect.send(newEffect)
        }
    }
}
