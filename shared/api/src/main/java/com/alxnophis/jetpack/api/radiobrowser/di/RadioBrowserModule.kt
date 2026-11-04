package com.alxnophis.jetpack.api.radiobrowser.di

import com.alxnophis.jetpack.api.radiobrowser.RadioBrowserRetrofitFactory
import com.alxnophis.jetpack.api.radiobrowser.RadioBrowserStationsService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal val radioBrowserApiModule: Module =
    module {
        single { RadioBrowserRetrofitFactory(androidContext()) }
        single { get<RadioBrowserRetrofitFactory>().createService(RadioBrowserStationsService::class.java) }
    }
