package com.alxnophis.jetpack.home.ui.contract

import android.content.Intent
import com.alxnophis.jetpack.core.base.viewmodel.UiAction
import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.home.domain.model.NavigationItem

internal sealed class HomeSideEffect : UiEffect {
    data class NavigateTo(val intent: Intent?) : HomeSideEffect()
}

internal sealed class HomeViewAction : UiAction {
    object ErrorDismissed : HomeViewAction()
    object LoadNavigationItems : HomeViewAction()
    data class NavigateTo(val intent: Intent?) : HomeViewAction()
}

internal data class HomeState(
    val isLoading: Boolean = false,
    val data: List<NavigationItem> = emptyList(),
    val error: Int? = null
) : UiState
