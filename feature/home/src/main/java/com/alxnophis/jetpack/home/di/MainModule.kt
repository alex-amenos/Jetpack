package com.alxnophis.jetpack.home.di

import com.alxnophis.jetpack.home.domain.usecase.UseCaseGetNavigationItems
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import com.alxnophis.jetpack.kotlin.utils.DefaultDispatcherProvider
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectHome() = loadHomeModules

private val loadHomeModules by lazy {
    loadKoinModules(
        listOf(
            homeModule
        )
    )
}

private val homeModule: Module = module {
    factory { UseCaseGetNavigationItems() }
    factory<DispatcherProvider> { DefaultDispatcherProvider() }
    viewModel {
        HomeViewModel(
            initialState = HomeState(),
            dispatchers = get(),
            useCaseGetNavigationItems = get()
        )
    }
}
