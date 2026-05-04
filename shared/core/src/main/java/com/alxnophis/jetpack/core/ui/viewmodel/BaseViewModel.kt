package com.alxnophis.jetpack.core.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import arrow.core.Either.Companion.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class BaseViewModel<Event : UiEvent, State : UiState>(
    initialState: State,
    private val savedStateHandle: SavedStateHandle? = null,
) : ViewModel() {
    val currentUiState: State
        get() = uiState.value

    @Suppress("PropertyName")
    protected val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    open val uiState = _uiState.asStateFlow()

    /**
     * Handles each Event
     */
    abstract fun handleEvent(event: Event)

    /**
     * Updates a new State
     */
    protected fun updateUiState(reduce: State.() -> State) {
        updateAndGetUiState(reduce)
    }

    /**
     * Updates a new State and get prior State
     */
    protected fun getPriorUiStateAndUpdate(reduce: State.() -> State): State {
        val oldState: State = currentUiState
        updateUiState(reduce)
        return oldState
    }

    /**
     * Updates and get a new State
     */
    protected fun updateAndGetUiState(reduce: State.() -> State): State =
        _uiState
            .updateAndGet { it.reduce() }
            .also { newState ->
                Timber.d("## Set new state: $newState")
                persistUiState(newState)
            }

    /**
     * Strip sensitive or desired fields before the state is written to [SavedStateHandle].
     * Override in subclasses to return a sanitized copy (e.g. with passwords cleared).
     * The default implementation returns the state unchanged.
     */
    protected open fun sanitizeForSavedState(state: State): State = state

    private fun persistUiState(newState: State) {
        if (savedStateHandle == null) return
        catch {
            val sanitizedState: State = sanitizeForSavedState(newState)
            savedStateHandle[SAVED_STATE_HANDLE_UI_STATE_KEY] = sanitizedState
            Timber.d("## Persisted state at savedStateHandle: $sanitizedState")
        }.onLeft { throwable ->
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
