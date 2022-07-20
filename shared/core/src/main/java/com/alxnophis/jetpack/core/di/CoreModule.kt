package com.alxnophis.jetpack.core.di

import com.alxnophis.jetpack.core.base.formatter.DateFormatter
import com.alxnophis.jetpack.kotlin.utils.DefaultDispatcherProvider
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreModule: Module = module() {
    factory<DispatcherProvider> { DefaultDispatcherProvider() }
    factory { DateFormatter() }
}
