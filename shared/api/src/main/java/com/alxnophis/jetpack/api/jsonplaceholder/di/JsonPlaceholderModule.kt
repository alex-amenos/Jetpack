package com.alxnophis.jetpack.api.jsonplaceholder.di

import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderApi
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderApiImpl
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal val jsonPlaceholderApiModule: Module = module {
    single { JsonPlaceholderRetrofitFactory().invoke() }
    factory<JsonPlaceholderApi> { JsonPlaceholderApiImpl(get()) }
}
