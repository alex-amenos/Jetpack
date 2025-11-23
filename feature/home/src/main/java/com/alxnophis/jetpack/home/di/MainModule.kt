package com.alxnophis.jetpack.home.di

import com.alxnophis.jetpack.home.domain.usecase.GetNavigationItemsUseCase
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val homeModule: Module =
    module {
        factory { GetNavigationItemsUseCase() }
        viewModel { HomeViewModel(get()) }
    }
