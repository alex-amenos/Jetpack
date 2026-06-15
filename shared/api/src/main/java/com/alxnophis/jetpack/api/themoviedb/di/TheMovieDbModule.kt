package com.alxnophis.jetpack.api.themoviedb.di

import com.alxnophis.jetpack.api.themoviedb.TheMovieDbMovieService
import com.alxnophis.jetpack.api.themoviedb.TheMovieDbRetrofitFactory
import com.alxnophis.jetpack.api.themoviedb.TheMovieDbSearchService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal val theMovieDbApiModule: Module = module {
    single { TheMovieDbRetrofitFactory(androidContext()) }
    single { get<TheMovieDbRetrofitFactory>().createService(TheMovieDbSearchService::class.java) }
    single { get<TheMovieDbRetrofitFactory>().createService(TheMovieDbMovieService::class.java) }
}