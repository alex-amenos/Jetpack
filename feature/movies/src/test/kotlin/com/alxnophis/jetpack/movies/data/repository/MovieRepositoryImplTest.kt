package com.alxnophis.jetpack.movies.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.themoviedb.TheMovieDbMovieService
import com.alxnophis.jetpack.api.themoviedb.TheMovieDbSearchService
import com.alxnophis.jetpack.api.themoviedb.model.MovieDetailsApiModel
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.domain.model.MovieDetailsMother
import com.alxnophis.jetpack.movies.domain.model.MovieError
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

private class MovieRepositoryImplTest {
    private val mockSearchService: TheMovieDbSearchService = mock()
    private val mockMovieService: TheMovieDbMovieService = mock()
    private val repository: MovieRepositoryImpl =
        MovieRepositoryImpl(
            searchService = mockSearchService,
            movieService = mockMovieService,
        )

    @Nested
    inner class SearchMovies {
        @Test
        fun `GIVEN a query WHEN searchMovies THEN returns Flow of PagingData`() {
            val query = "Batman"

            val result = repository.searchMovies(query)

            assertNotNull(result)
        }
    }

    @Nested
    inner class GetMovieDetails {
        @Test
        fun `GIVEN successful API response WHEN getMovieDetails THEN returns movies details`() =
            runTest {
                whenever(mockMovieService.getMovieDetails(movieId = any(), language = any())).thenReturn(movieDetailsApiModel.right())

                val result: Either<MovieError, MovieDetails> = repository.getMovieDetails(MOVIE_ID)

                assertEquals(
                    movieDetails.right(),
                    result,
                )
            }

        @Test
        fun `GIVEN API returns HttpError 401 WHEN getMovieDetails THEN returns Unauthorized error`() =
            runTest {
                val error = HttpError(code = 401, message = UNAUTHORIZED_ERROR_MESSAGE, body = BODY)
                whenever(mockMovieService.getMovieDetails(movieId = any(), language = any())).thenReturn(error.left())

                val result: Either<MovieError, MovieDetails> = repository.getMovieDetails(MOVIE_ID)

                assertEquals(
                    MovieError.Unauthorized.left(),
                    result,
                )
            }

        @Test
        fun `GIVEN API returns HttpError 404 WHEN getMovieDetails THEN returns NotFound error`() =
            runTest {
                val error = HttpError(code = 404, message = NOT_FOUND_ERROR_MESSAGE, body = BODY)
                whenever(mockMovieService.getMovieDetails(movieId = any(), language = any())).thenReturn(error.left())

                val result: Either<MovieError, MovieDetails> = repository.getMovieDetails(MOVIE_ID)

                assertEquals(
                    MovieError.NotFound.left(),
                    result,
                )
            }

        @Test
        fun `GIVEN API returns HttpError 500 WHEN getMovieDetails THEN returns Unknown error`() =
            runTest {
                val error = HttpError(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, body = BODY)
                whenever(mockMovieService.getMovieDetails(movieId = any(), language = any())).thenReturn(error.left())

                val result: Either<MovieError, MovieDetails> = repository.getMovieDetails(MOVIE_ID)

                assertEquals(
                    MovieError
                        .Unknown(INTERNAL_SERVER_ERROR_MESSAGE)
                        .left(),
                    result,
                )
            }

        @Test
        fun `GIVEN API returns IOError WHEN getMovieDetails THEN returns Network error`() =
            runTest {
                val cause = IOException(NETWORK_ERROR_MESSAGE)
                val error = IOError(cause)
                whenever(mockMovieService.getMovieDetails(movieId = any(), language = any())).thenReturn(error.left())

                val result: Either<MovieError, MovieDetails> = repository.getMovieDetails(MOVIE_ID)

                assertEquals(
                    MovieError.Network.left(),
                    result,
                )
            }

        @Test
        fun `GIVEN API returns UnexpectedCallError WHEN getMovieDetails THEN returns Either Left MovieError Unknown`() =
            runTest {
                val cause = Throwable(UNEXPECTED_FAILURE_MESSAGE)
                val error = UnexpectedCallError(cause)
                whenever(mockMovieService.getMovieDetails(movieId = any(), language = any())).thenReturn(error.left())

                val result: Either<MovieError, MovieDetails> = repository.getMovieDetails(MOVIE_ID)

                assertEquals(
                    MovieError
                        .Unknown(message = UNEXPECTED_FAILURE_MESSAGE)
                        .left(),
                    result,
                )
            }
    }

    private companion object {
        const val MOVIE_ID = 123
        const val BODY = "{}"
        const val UNAUTHORIZED_ERROR_MESSAGE = "Unauthorized"
        const val NOT_FOUND_ERROR_MESSAGE = "Not Found"
        const val INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error"
        const val NETWORK_ERROR_MESSAGE = "Network failed"
        const val UNEXPECTED_FAILURE_MESSAGE = "Unexpected failure"

        val movieDetailsApiModel =
            MovieDetailsApiModel(
                id = 123,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                runtime = 120,
                status = "Released",
                tagline = "Tagline",
            )
        val movieDetails =
            MovieDetailsMother(
                id = 123,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                runtime = 120,
                status = "Released",
                tagline = "Tagline",
            )
    }
}
