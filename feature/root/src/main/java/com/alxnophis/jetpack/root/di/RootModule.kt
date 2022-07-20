package com.alxnophis.jetpack.root.di

import com.alxnophis.jetpack.root.ui.viewmodel.RootViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectRoot() = loadRootModules

private val loadRootModules by lazy {
    loadKoinModules(rootModule)
}

private val rootModule: Module = module {
    viewModel {
        RootViewModel()
    }
}
