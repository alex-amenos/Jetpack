package com.alxnophis.jetpack.home.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.home.R
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.domain.usecase.UseCaseGetNavigationItems
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeState
import kotlinx.coroutines.launch

internal class HomeViewModel(
    initialState: HomeState,
    private val useCaseGetNavigationItems: UseCaseGetNavigationItems
) : BaseViewModel<HomeEvent, HomeState>(initialState) {

    init {
        handleEvent(HomeEvent.LoadNavigationItems)
    }

    override fun handleEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                HomeEvent.ErrorDismissed -> dismissError()
                HomeEvent.LoadNavigationItems -> loadNavigationItems()
            }
        }
    }

    private fun loadNavigationItems() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            getNavigationItems()
                .fold(
                    {
                        updateState {
                            copy(
                                isLoading = false,
                                error = R.string.home_error_loading_navigation_items
                            )
                        }
                    },
                    { navigationItems ->
                        updateState {
                            copy(
                                isLoading = false,
                                data = navigationItems
                            )
                        }
                    }
                )
        }
    }

    private suspend fun getNavigationItems(): Either<NavigationError, List<NavigationItem>> = useCaseGetNavigationItems.invoke()

    private fun dismissError() {
        viewModelScope.launch {
            updateState { copy(error = null) }
        }
    }
}
