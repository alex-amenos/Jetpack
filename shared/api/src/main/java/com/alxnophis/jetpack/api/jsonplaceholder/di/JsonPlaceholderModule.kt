package com.alxnophis.jetpack.api.jsonplaceholder.di

import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal val jsonPlaceholderApiModule: Module =
    module {
        single { JsonPlaceholderRetrofitFactory(androidContext()).invoke() }
    }
