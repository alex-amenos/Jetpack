package com.alxnophis.jetpack.core.di

import com.alxnophis.jetpack.api.di.apiModule
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule: Module = module {
    loadKoinModules(
        listOf(
            apiModule
        )
    )
}
