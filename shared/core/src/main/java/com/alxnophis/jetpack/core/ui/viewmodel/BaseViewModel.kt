package com.alxnophis.jetpack.core.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import arrow.core.Either.Companion.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class BaseViewModel<Event : UiEvent, State : UiState>(
    initialState: State,
    private val savedStateHandle: SavedStateHandle? = null,
) : ViewModel() {
    val currentState: State
        get() = uiState.value

    @Suppress("PropertyName")
    protected val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    open val uiState = _uiState.asStateFlow()

    /**
     * Handle each Event
     */
    abstract fun handleEvent(event: Event)

    /**
     * Update a new State
     */
    protected fun updateUiState(reduce: State.() -> State) {
        _uiState
            .update { it.reduce() }
            .also { Timber.d("## Set new state: $currentState") }
    }

    /**
     * Update a new State and get prior State
     */
    protected fun getPriorUiStateAndUpdate(reduce: State.() -> State): State =
        _uiState
            .getAndUpdate { it.reduce() }
            .also { Timber.d("## Set new state: $currentState") }

    /**
     * Update and get a new State
     */
    protected fun updateAndGetUiState(reduce: State.() -> State): State =
        _uiState
            .updateAndGet { it.reduce() }
            .also { newState -> Timber.d("## Set new state: $newState") }

    /**
     * Strip sensitive fields before the state is written to [SavedStateHandle].
     * Override in subclasses to return a sanitized copy (e.g. with passwords cleared).
     * The default implementation returns the state unchanged.
     */
    protected open fun sanitizeForSavedState(state: State): State = state

    /**
     * Update and persist a new State in SavedStateHandle
     * Note: State must implement Parcelable or be a primitive type to be saved
     */
    protected fun updateAndPersistUiState(reduce: State.() -> State) {
        _uiState
            .updateAndGet { it.reduce() }
            .also { newState ->
                catch {
                    savedStateHandle?.set(SAVED_STATE_HANDLE_UI_STATE_KEY, sanitizeForSavedState(newState))
                }.fold(
                    { throwable ->
                        Timber.w(
                            throwable,
                            "## Failed to persist state to SavedStateHandle [key=$SAVED_STATE_HANDLE_UI_STATE_KEY, type=${newState::class.qualifiedName}]",
                        )
                    },
                    {
                        Timber.d("## Set new state: $newState")
                    },
                )
            }
    }

    companion object {
        const val SAVED_STATE_HANDLE_UI_STATE_KEY = "savedStateHandleUiStateKey"
    }
}
