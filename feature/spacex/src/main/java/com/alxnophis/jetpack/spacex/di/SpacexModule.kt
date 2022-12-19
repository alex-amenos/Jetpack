package com.alxnophis.jetpack.spacex.di

import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepository
import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepositoryImpl
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.spacex.ui.viewmodel.LaunchesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectSpacex() = loadSpacexModules

private val loadSpacexModules by lazy {
    loadKoinModules(spacexModule)
}

private val spacexModule: Module = module {
    factory<LaunchesRepository> {
        LaunchesRepositoryImpl(
            dispatcherProvider = get(),
            apiDataSource = get()
        )
    }
    viewModel {
        LaunchesViewModel(
            initialState = LaunchesState(),
            dateFormatter = get(),
            randomProvider = get(),
            dispatcherProvider = get(),
            launchesRepository = get(),
        )
    }
}
