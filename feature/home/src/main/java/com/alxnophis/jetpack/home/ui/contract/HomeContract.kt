package com.alxnophis.jetpack.home.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.home.domain.model.NavigationItem

internal const val NO_ERROR = 0

internal sealed class HomeEvent : UiEvent {
    object ErrorDismissed : HomeEvent()
    object LoadNavigationItems : HomeEvent()
}

@optics
internal data class HomeState(
    val isLoading: Boolean = false,
    val data: List<NavigationItem> = emptyList(),
    val error: Int = NO_ERROR
) : UiState {
    companion object
}
