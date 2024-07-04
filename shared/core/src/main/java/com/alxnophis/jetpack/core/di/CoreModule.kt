package com.alxnophis.jetpack.core.di

import com.alxnophis.jetpack.core.base.provider.BaseRandomProvider
import com.alxnophis.jetpack.core.ui.formatter.BaseDateFormatter
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule: Module = module {
    factory { BaseDateFormatter() }
    factory { BaseRandomProvider() }
}
