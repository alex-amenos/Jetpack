package com.alxnophis.jetpack.api.di

import com.alxnophis.jetpack.api.jsonplaceholder.di.jsonPlaceholderApiModule
import com.alxnophis.jetpack.api.themoviedb.di.theMovieDbApiModule
import org.koin.core.module.Module
import org.koin.dsl.module

val apiModule: Module = module {
    includes(jsonPlaceholderApiModule)
    includes(theMovieDbApiModule)
}
