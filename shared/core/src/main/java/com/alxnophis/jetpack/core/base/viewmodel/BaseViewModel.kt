package com.alxnophis.jetpack.core.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * MVI Architecture with Kotlin Flows and Channels by Yusuf Ceylan
 * POST Idea: https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d
 * REPOSITORY idea: https://github.com/yusufceylan/MVI-Playground
 */
abstract class BaseViewModel<Action : UiAction, State : UiState>(
    initialState: State
) : ViewModel() {

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _action: MutableSharedFlow<Action> = MutableSharedFlow()
    val action = _action.asSharedFlow()

    init {
        subscribeActions()
    }

    /**
     * Start listening to Action
     */
    private fun subscribeActions() {
        viewModelScope.launch {
            action.collect {
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
        val newAction = action
        Timber.d("## Set new action: $newAction")
        viewModelScope.launch { _action.emit(newAction) }
    }

    /**
     * Set new State
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        Timber.d("## Set new state: $newState")
        _uiState.value = newState
    }
}
