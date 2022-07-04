package com.alxnophis.jetpack.root.di

import com.alxnophis.jetpack.core.di.coreModule
import com.alxnophis.jetpack.root.ui.viewmodel.RootViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun injectRoot() = loadRootModules

private val loadRootModules by lazy {
    loadKoinModules(
        listOf(
            coreModule,
            rootModule
        )
    )
}

private val rootModule: Module = module {
    viewModel {
        RootViewModel()
    }
}
