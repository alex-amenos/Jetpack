package com.alxnophis.jetpack.home.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.domain.model.NavigationItem

internal const val NO_ERROR = 0

internal sealed class HomeEvent : UiEvent {
    data object Initialized : HomeEvent()

    data object ErrorDismissRequested : HomeEvent()

    data object GoBackRequested : HomeEvent()

    data class NavigationRequested(val feature: Feature) : HomeEvent()
}

@optics
internal data class HomeState(
    val isLoading: Boolean,
    val data: List<NavigationItem>,
    val error: Int,
) : UiState {
    internal companion object {
        val initialState =
            HomeState(
                isLoading = false,
                data = emptyList(),
                error = NO_ERROR,
            )
    }
}
