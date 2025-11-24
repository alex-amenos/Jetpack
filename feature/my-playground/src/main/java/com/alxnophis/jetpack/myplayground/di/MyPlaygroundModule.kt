package com.alxnophis.jetpack.myplayground.di

import com.alxnophis.jetpack.myplayground.ui.viewmodel.MyPlaygroundViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val myPlaygroundModule: Module =
    module {
        viewModel { MyPlaygroundViewModel() }
    }
