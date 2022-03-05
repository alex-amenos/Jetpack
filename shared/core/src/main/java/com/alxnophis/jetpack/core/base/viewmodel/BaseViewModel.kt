package com.alxnophis.jetpack.core.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * MVI Architecture with Kotlin Flows and Channels by Yusuf Ceylan
 * POST Idea: https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d
 * REPOSITORY idea: https://github.com/yusufceylan/MVI-Playground
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseViewModel<Action : UiAction, State : UiState, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _action: MutableSharedFlow<Action> = MutableSharedFlow()
    val uiAction = _action.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

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
        viewModelScope.launch { _action.emit(action) }
    }

    /**
     * Set new State
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        Timber.d("## Set new state: $newState")
        _uiState.value = newState
    }

    /**
     * Set new Effect
     */
    protected fun setEffect(builder: () -> Effect) {
        val newEffect = builder()
        Timber.d("## Set new effect: $newEffect")
        viewModelScope.launch { _effect.send(newEffect) }
    }
}
