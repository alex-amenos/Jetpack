package com.alxnophis.jetpack.movies.data.repository

import androidx.paging.PagingData
import arrow.core.Either
import arrow.core.right
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.domain.model.MovieDetailsMother
import com.alxnophis.jetpack.movies.domain.model.MovieError
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeMovieRepository : MovieRepository {
    var searchMoviesResult: Flow<PagingData<Movie>> = flowOf(PagingData.empty())
    var movieDetailsResult: Either<MovieError, MovieDetails> = movieDetails.right()

    var searchMoviesCallCount = ZERO_INT
        private set
    var getMovieDetailsCallCount = ZERO_INT
        private set
    var lastSearchQuery: String? = null
        private set
    var lastGetMovieDetailsId: Int? = null
        private set

    override fun searchMovies(query: String): Flow<PagingData<Movie>> {
        searchMoviesCallCount++
        lastSearchQuery = query
        return searchMoviesResult
    }

    override suspend fun getMovieDetails(id: Int): Either<MovieError, MovieDetails> {
        getMovieDetailsCallCount++
        lastGetMovieDetailsId = id
        return movieDetailsResult
    }

    companion object {
        val movieDetails =
            MovieDetailsMother(
                id = 1,
                title = "Fake Movie",
                overview = "Fake",
                posterPath = null,
                backdropPath = null,
                releaseDate = "2024-01-01",
                voteAverage = 5.0,
                runtime = 100,
                status = "Released",
                tagline = "Fake Tagline",
            )
    }
}
