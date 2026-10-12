@file:Suppress("ktlint:standard:filename")

package com.alxnophis.jetpack.home.ui.contract

import androidx.compose.runtime.Immutable
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.domain.usecase.GetNavigationItemsUseCase

@Immutable
internal data class HomeUiState(
    val data: List<NavigationItem> = GetNavigationItemsUseCase.defaultNavigationItems,
) : UiState {
    internal companion object {
        val initialState = HomeUiState()
    }
}
