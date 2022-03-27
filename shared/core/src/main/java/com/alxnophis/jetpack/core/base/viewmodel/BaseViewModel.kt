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
abstract class BaseViewModel<Action : UiAction, State : UiState, SideEffect : UiEffect>(
    initialState: State
) : ViewModel() {

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _action: MutableSharedFlow<Action> = MutableSharedFlow()
    val uiAction = _action.asSharedFlow()

    private val _sideEffect: Channel<SideEffect> = Channel(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        subscribeActions()
    }

    /**
     * Start listening to Action
     */
    private fun subscribeActions() {
        viewModelScope.launch {
            uiAction.collect {
                handleAction(it)
            }
        }
    }

    /**
     * Handle each Action
     */
    abstract fun handleAction(action: Action)

    /**
     * Set new Action
     */
    fun setAction(action: Action) {
        Timber.d("## Set new action: $action")
        viewModelScope.launch {
            _action.emit(action)
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
    protected fun setSideEffect(builder: () -> SideEffect) {
        val newSideEffect = builder()
        Timber.d("## Set new SideEffect: $newSideEffect")
        viewModelScope.launch {
            _sideEffect.send(newSideEffect)
        }
    }
}
