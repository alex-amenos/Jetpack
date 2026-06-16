package com.alxnophis.jetpack.movies.di

import com.alxnophis.jetpack.movies.data.repository.MovieRepositoryImpl
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import com.alxnophis.jetpack.movies.ui.viewmodel.MovieDetailViewModel
import com.alxnophis.jetpack.movies.ui.viewmodel.MoviesViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val moviesModule: Module =
    module {
        factory<MovieRepository> {
            MovieRepositoryImpl(
                searchService = get(),
                movieService = get(),
            )
        }
        viewModel { MoviesViewModel(get(), get()) }
        viewModel { MovieDetailViewModel(get(), get()) }
    }
