package com.alxnophis.jetpack.movies.di

import com.alxnophis.jetpack.movies.data.repository.MovieRepositoryImpl
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import org.koin.core.module.Module
import org.koin.dsl.module

internal val moviesModule: Module = module {
    factory<MovieRepository> {
        MovieRepositoryImpl(
            searchService = get(),
            movieService = get(),
        )
    }
}
