package com.alxnophis.jetpack.home.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.home.R
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.domain.usecase.GetNavigationItemsUseCase
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.contract.NO_ERROR
import com.alxnophis.jetpack.home.ui.contract.data
import com.alxnophis.jetpack.home.ui.contract.error
import com.alxnophis.jetpack.home.ui.contract.isLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val getNavigationItemsUseCase: GetNavigationItemsUseCase,
    initialState: HomeState = HomeState.initialState,
) : BaseViewModel<HomeEvent, HomeState>(initialState) {
    private var navigationItemsJob: Job? = null

    override val uiState: StateFlow<HomeState> =
        _uiState
            .onStart {
                loadNavigationItems()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = initialState,
            )

    override fun handleEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                HomeEvent.ErrorDismissRequested -> dismissError()
                HomeEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented")
                is HomeEvent.NavigationRequested -> throw IllegalStateException("Navigation not implemented")
            }
        }
    }

    private fun loadNavigationItems() {
        if (navigationItemsJob != null) return
        navigationItemsJob =
            viewModelScope.launch {
                _uiState.updateCopy {
                    HomeState.isLoading set true
                }
                getNavigationItems().fold(
                    {
                        _uiState.updateCopy {
                            HomeState.isLoading set false
                            HomeState.error set R.string.home_error_loading_navigation_items
                        }
                    },
                    { navigationItems ->
                        _uiState.updateCopy {
                            HomeState.isLoading set false
                            HomeState.data set navigationItems
                        }
                    },
                )
            }
    }

    private suspend fun getNavigationItems(): Either<NavigationError, List<NavigationItem>> = getNavigationItemsUseCase.invoke()

    private fun dismissError() {
        viewModelScope.launch {
            _uiState.updateCopy {
                HomeState.error set NO_ERROR
            }
        }
    }
}
