package com.alxnophis.jetpack.musicdashboard.di

import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun injectMusicDashboard() = loadMusicDashboardModules

private val loadMusicDashboardModules by lazy {
    loadKoinModules(
        listOf()
    )
}

private val loadMusicDashboardModule: Module = module {
}
