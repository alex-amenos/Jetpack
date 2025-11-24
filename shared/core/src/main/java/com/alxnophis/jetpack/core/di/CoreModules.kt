package com.alxnophis.jetpack.core.di

import com.alxnophis.jetpack.api.di.apiModule
import org.koin.core.module.Module

val coreModules: List<Module> =
    listOf(
        coreModule,
        apiModule,
    )
