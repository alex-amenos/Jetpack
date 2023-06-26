package com.alxnophis.jetpack.home.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.home.domain.model.NavigationItem

internal const val NO_ERROR = 0

internal sealed class HomeEvent : UiEvent {
    object Initialized : HomeEvent()
    object ErrorDismissRequested : HomeEvent()
}

@optics
internal data class HomeState(
    val isLoading: Boolean,
    val data: List<NavigationItem>,
    val error: Int
) : UiState {
    internal companion object {
        val initialState = HomeState(
            isLoading = false,
            data = emptyList(),
            error = NO_ERROR
        )
    }
}
