package com.alxnophis.jetpack.home.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.alxnophis.jetpack.core.extensions.asStateFlow
import com.alxnophis.jetpack.home.domain.usecase.GetNavigationItemsUseCase
import com.alxnophis.jetpack.home.ui.contract.HomeUiState
import kotlinx.coroutines.flow.StateFlow

internal class HomeViewModel(
    getNavigationItemsUseCase: GetNavigationItemsUseCase,
    initialState: HomeUiState = HomeUiState(data = getNavigationItemsUseCase()),
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = initialState.asStateFlow()
}
