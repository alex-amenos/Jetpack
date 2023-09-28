package com.alxnophis.jetpack.home.di

import com.alxnophis.jetpack.home.domain.usecase.GetNavigationItemsUseCase
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectHome() = loadHomeModules

private val loadHomeModules by lazy {
    loadKoinModules(homeModule)
}

private val homeModule: Module = module {
    factory { GetNavigationItemsUseCase() }
    viewModel { HomeViewModel(get()) }
}
