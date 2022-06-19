package com.alxnophis.jetpack.home.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.home.domain.model.NavigationItem

internal sealed class HomeEvent : UiEvent {
    object ErrorDismissed : HomeEvent()
    object LoadNavigationItems : HomeEvent()
}

internal data class HomeState(
    val isLoading: Boolean = false,
    val data: List<NavigationItem> = emptyList(),
    val error: Int? = null
) : UiState
