package com.alxnophis.jetpack.core.di

import com.alxnophis.jetpack.core.base.formatter.BaseDateFormatter
import com.alxnophis.jetpack.core.base.provider.BaseRandomProvider
import com.alxnophis.jetpack.kotlin.utils.DefaultDispatcherProvider
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule: Module = module() {
    factory<DispatcherProvider> { DefaultDispatcherProvider() }
    factory { BaseDateFormatter() }
    factory { BaseRandomProvider() }
}
