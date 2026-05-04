package com.alxnophis.jetpack.core.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import timber.log.Timber

@Suppress("unused")
abstract class BaseViewModel<Event : UiEvent, State : UiState>(
    initialUiState: State,
    private val savedStateHandle: SavedStateHandle? = null,
) : ViewModel() {
    val currentUiState: State
        get() = uiState.value

    @Suppress("PropertyName")
    protected val _uiState: MutableStateFlow<State> =
        MutableStateFlow(
            runCatching { savedStateHandle?.get<State>(SAVED_STATE_HANDLE_UI_STATE_KEY) }
                .onFailure { Timber.w(it, "## Failed to restore state from SavedStateHandle — falling back to initial state") }
                .getOrNull() ?: initialUiState,
        )

    open val uiState = _uiState.asStateFlow()

    /**
     * Handles each Event.
     */
    abstract fun handleEvent(event: Event)

    /**
     * Atomically updates state using [reduce].
     */
    protected fun updateUiState(reduce: State.() -> State) =
        _uiState
            .updateAndGet { it.reduce() }
            .also { newState ->
                Timber.d("## Set new state: $newState")
                persistUiState(newState)
            }

    /**
     * Atomically updates state using [reduce] and returns the new state.
     */
    protected fun updateAndGetUiState(reduce: State.() -> State): State =
        _uiState
            .updateAndGet { it.reduce() }
            .also { newState ->
                Timber.d("## Set new state: $newState")
                persistUiState(newState)
            }

    /**
     * Atomically updates state using [reduce] and returns the prior state.
     * The new state is logged and persisted to [SavedStateHandle] after the update.
     */
    protected fun getPriorUiStateAndUpdate(reduce: State.() -> State): State {
        var oldState: State = _uiState.value
        val newState =
            _uiState.updateAndGet {
                oldState = it
                it.reduce()
            }
        Timber.d("## Set new state: $newState")
        persistUiState(newState)
        return oldState
    }

    /**
     * Strip sensitive or desired fields before the state is written to [SavedStateHandle].
     * Override in subclasses to return a sanitized copy (e.g. with passwords cleared).
     * The default implementation returns the state unchanged.
     */
    protected open fun sanitizeForSavedState(state: State): State = state

    private fun persistUiState(newState: State) {
        val handle = savedStateHandle ?: return
        runCatching {
            val sanitizedState: State = sanitizeForSavedState(newState)
            handle[SAVED_STATE_HANDLE_UI_STATE_KEY] = sanitizedState
            Timber.d("## Persisted state at savedStateHandle: $sanitizedState")
        }.onFailure { throwable ->
            Timber.w(
                throwable,
                "## Failed to persist state to SavedStateHandle [key=${SAVED_STATE_HANDLE_UI_STATE_KEY}, type=${newState::class.qualifiedName}]",
            )
        }
    }

    companion object {
        const val SAVED_STATE_HANDLE_UI_STATE_KEY = "savedStateHandleUiStateKey"
    }
}
