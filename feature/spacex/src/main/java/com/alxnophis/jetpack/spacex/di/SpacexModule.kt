package com.alxnophis.jetpack.spacex.di

import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepository
import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepositoryImpl
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
    factory<LaunchesRepository> { LaunchesRepositoryImpl(get()) }
    viewModel { LaunchesViewModel(get(), get(), get()) }
}
