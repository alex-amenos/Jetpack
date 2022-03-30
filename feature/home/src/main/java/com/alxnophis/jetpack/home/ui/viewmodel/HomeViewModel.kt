package com.alxnophis.jetpack.home.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.home.R
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.domain.usecase.UseCaseGetNavigationItems
import com.alxnophis.jetpack.home.ui.contract.HomeSideEffect
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.contract.HomeViewAction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class HomeViewModel(
    initialState: HomeState = HomeState(),
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val useCaseGetNavigationItems: UseCaseGetNavigationItems
) : BaseViewModel<HomeViewAction, HomeState, HomeSideEffect>(initialState) {

    init {
        setAction(HomeViewAction.LoadNavigationItems)
    }

    override fun handleAction(action: HomeViewAction) {
        when (action) {
            HomeViewAction.ErrorDismissed -> dismissError()
            HomeViewAction.LoadNavigationItems -> loadNavigationItems()
            is HomeViewAction.NavigateTo -> setSideEffect { HomeSideEffect.NavigateTo(action.intent) }
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
        withContext(dispatcherIO) {
            useCaseGetNavigationItems.invoke()
        }

    private fun dismissError() {
        viewModelScope.launch {
            setState { copy(error = null) }
        }
    }
}