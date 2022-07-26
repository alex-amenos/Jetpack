package com.alxnophis.jetpack.api.spacex.di

import com.alxnophis.jetpack.api.spacex.SpacexApi
import com.alxnophis.jetpack.api.spacex.SpacexApiImpl
import com.alxnophis.jetpack.api.spacex.SpacexApolloClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal val spacexApiModule: Module = module {
    single { SpacexApolloClientFactory().invoke() }
    factory<SpacexApi> { SpacexApiImpl(get()) }
}
