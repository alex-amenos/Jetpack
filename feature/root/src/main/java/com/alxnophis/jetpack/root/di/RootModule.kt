package com.alxnophis.jetpack.root.di

import com.alxnophis.jetpack.root.ui.viewmodel.RootViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val rootModule: Module =
    module {
        viewModel { RootViewModel(get(), get()) }
    }
