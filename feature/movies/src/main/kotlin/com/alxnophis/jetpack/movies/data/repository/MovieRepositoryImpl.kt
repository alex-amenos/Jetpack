package com.alxnophis.jetpack.movies.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.themoviedb.TheMovieDbMovieService
import com.alxnophis.jetpack.api.themoviedb.TheMovieDbSearchService
import com.alxnophis.jetpack.movies.data.datasource.MoviePagingSource
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.domain.model.MovieError
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

internal class MovieRepositoryImpl(
    private val searchService: TheMovieDbSearchService,
    private val movieService: TheMovieDbMovieService,
) : MovieRepository {
    override fun searchMovies(query: String): Flow<PagingData<Movie>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = PAGE_SIZE,
                    enablePlaceholders = false,
                    prefetchDistance = PAGE_SIZE / 2,
                ),
            pagingSourceFactory = {
                MoviePagingSource(searchService, query)
            },
        ).flow

    override suspend fun getMovieDetails(id: Int): Either<MovieError, MovieDetails> =
        movieService
            .getMovieDetails(id)
            .mapLeft { callError ->
                when (callError) {
                    is IOError -> {
                        MovieError.Network
                    }

                    is HttpError -> {
                        when (callError.code) {
                            401 -> MovieError.Unauthorized
                            404 -> MovieError.NotFound
                            else -> MovieError.Unknown(callError.message)
                        }
                    }

                    is UnexpectedCallError -> {
                        MovieError.Unknown(callError.cause.message)
                    }
                }
            }.map {
                MovieDetails(
                    id = it.id,
                    title = it.title,
                    overview = it.overview,
                    posterPath = it.posterPath,
                    backdropPath = it.backdropPath,
                    releaseDate = it.releaseDate,
                    voteAverage = it.voteAverage,
                    runtime = it.runtime,
                    status = it.status,
                    tagline = it.tagline,
                )
            }

    companion object {
        const val PAGE_SIZE = 20
    }
}
