package com.alxnophis.jetpack.myplayground.di

import com.alxnophis.jetpack.myplayground.ui.viewmodel.MyPlaygroundViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectMyPlayground() = loadMyPlaygroundModules

private val loadMyPlaygroundModules by lazy {
    loadKoinModules(
        listOf(myPlaygroundModule),
    )
}

private val myPlaygroundModule: Module =
    module {
        viewModel { MyPlaygroundViewModel() }
    }
