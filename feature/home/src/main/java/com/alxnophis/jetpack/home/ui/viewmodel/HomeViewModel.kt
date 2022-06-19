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
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class HomeViewModel(
    initialState: HomeState,
    private val dispatchers: DispatcherProvider,
    private val useCaseGetNavigationItems: UseCaseGetNavigationItems
) : BaseViewModel<HomeEvent, HomeState>(initialState) {

    init {
        setEvent(HomeEvent.LoadNavigationItems)
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.ErrorDismissed -> dismissError()
            HomeEvent.LoadNavigationItems -> loadNavigationItems()
        }
    }

    private fun loadNavigationItems() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            getNavigationItems()
                .fold(
                    {
                        setState {
                            copy(
                                isLoading = false,
                                error = R.string.home_error_loading_navigation_items
                            )
                        }
                    },
                    { navigationItems ->
                        setState {
                            copy(
                                isLoading = false,
                                data = navigationItems
                            )
                        }
                    }
                )
        }
    }

    private suspend fun getNavigationItems(): Either<NavigationError, List<NavigationItem>> =
        withContext(dispatchers.io()) {
            useCaseGetNavigationItems.invoke()
        }

    private fun dismissError() {
        viewModelScope.launch {
            setState { copy(error = null) }
        }
    }
}
