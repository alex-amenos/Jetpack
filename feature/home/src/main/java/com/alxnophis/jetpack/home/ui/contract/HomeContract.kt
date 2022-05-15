package com.alxnophis.jetpack.home.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.router.screen.Screen

internal sealed class HomeEffect : UiEffect {
    object ExitApp : HomeEffect()
    data class NavigateTo(val screen: Screen) : HomeEffect()
}

internal sealed class HomeEvent : UiEvent {
    object ExitApp : HomeEvent()
    object ErrorDismissed : HomeEvent()
    object LoadNavigationItems : HomeEvent()
    data class NavigateTo(val screen: Screen) : HomeEvent()
}

internal data class HomeState(
    val isLoading: Boolean = false,
    val data: List<NavigationItem> = emptyList(),
    val error: Int? = null
) : UiState
