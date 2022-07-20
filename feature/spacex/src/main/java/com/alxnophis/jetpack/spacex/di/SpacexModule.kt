package com.alxnophis.jetpack.spacex.di

import com.alxnophis.jetpack.api.di.apiModule
import com.alxnophis.jetpack.core.di.coreModule
import com.alxnophis.jetpack.spacex.data.datasource.LaunchesDataSource
import com.alxnophis.jetpack.spacex.data.datasource.LaunchesDataSourceImpl
import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepository
import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepositoryImpl
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectSpacex() = loadSpacexModules

private val loadSpacexModules by lazy {
    loadKoinModules(
        listOf(
            coreModule,
            apiModule,
            spacexModule
        )
    )
}

private val spacexModule: Module = module {
    factory<LaunchesDataSource> { LaunchesDataSourceImpl(get(), get()) }
    factory<LaunchesRepository> { LaunchesRepositoryImpl(get()) }
}
