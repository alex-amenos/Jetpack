package com.alxnophis.jetpack.core.di

import com.alxnophis.jetpack.core.base.provider.BaseRandomProvider
import com.alxnophis.jetpack.core.ui.formatter.BaseDateFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreModule: Module =
    module {
        factory { BaseDateFormatter() }
        factory { BaseRandomProvider() }

        // Application-level CoroutineScope that lives for the entire app lifecycle
        // Uses IO dispatcher since background work (network/database) is I/O-bound
        single(named("applicationScope")) {
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }
