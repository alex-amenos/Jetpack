package com.alxnophis.jetpack.home.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.copy
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
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
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val getNavigationItemsUseCase: GetNavigationItemsUseCase,
    initialState: HomeState = HomeState.initialState,
    initialEvent: HomeEvent? = HomeEvent.Initialized
) : BaseViewModel<HomeEvent, HomeState>(initialState) {

    init {
        initialEvent?.let(::handleEvent)
    }

    override fun handleEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                HomeEvent.Initialized -> loadNavigationItems()
                HomeEvent.ErrorDismissedRequested -> dismissError()
            }
        }
    }

    private fun loadNavigationItems() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }
            getNavigationItems()
                .fold(
                    {
                        updateUiState {
                            copy {
                                HomeState.isLoading set false
                                HomeState.error set R.string.home_error_loading_navigation_items
                            }
                        }
                    },
                    { navigationItems ->
                        updateUiState {
                            copy {
                                HomeState.isLoading set false
                                HomeState.data set navigationItems
                            }
                        }
                    }
                )
        }
    }

    private suspend fun getNavigationItems(): Either<NavigationError, List<NavigationItem>> = getNavigationItemsUseCase.invoke()

    private fun dismissError() {
        viewModelScope.launch {
            updateUiState {
                copy {
                    HomeState.error set NO_ERROR
                }
            }
        }
    }
}
