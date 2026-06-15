package com.alxnophis.jetpack.movies.domain.repository

import androidx.paging.PagingData
import arrow.core.Either
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.domain.model.MovieError
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun searchMovies(query: String): Flow<PagingData<Movie>>
    suspend fun getMovieDetails(id: Int): Either<MovieError, MovieDetails>
}
