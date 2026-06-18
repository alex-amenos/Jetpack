package com.alxnophis.jetpack.movies.data.datasource

import androidx.paging.PagingSource
import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.themoviedb.TheMovieDbSearchService
import com.alxnophis.jetpack.api.themoviedb.model.MovieApiModel
import com.alxnophis.jetpack.api.themoviedb.model.MovieSearchResponseApiModel
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.model.MovieError
import com.alxnophis.jetpack.movies.domain.model.MovieException
import com.alxnophis.jetpack.movies.domain.model.MovieMother
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

private class MoviePagingSourceTest {
    private val mockSearchService: TheMovieDbSearchService = mock()

    @Test
    fun `GIVEN empty query WHEN load THEN returns LoadResult Page with empty list and null keys`() =
        runTest {
            val pagingSource = MoviePagingSource(mockSearchService, EMPTY)

            val result: PagingSource.LoadResult<Int, Movie> =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertEquals(
                PagingSource.LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null),
                result,
            )
        }

    @Test
    fun `GIVEN successful API response page 1 of 5 WHEN load THEN returns LoadResult Page with nextKey 2 and prevKey null`() =
        runTest {
            val pagingSource = MoviePagingSource(mockSearchService, QUERY)
            val apiResponse =
                MovieSearchResponseApiModel(
                    page = 1,
                    results = listOf(movieApiModel),
                    totalPages = 5,
                    totalResults = 1,
                )

            whenever(
                mockSearchService.searchMovies(
                    query = any(),
                    page = any(),
                    includeAdult = any(),
                    language = any(),
                ),
            ).thenReturn(apiResponse.right())

            val result: PagingSource.LoadResult<Int, Movie> =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertEquals(
                PagingSource.LoadResult.Page(data = listOf(movie), prevKey = null, nextKey = 2),
                result,
            )
        }

    @Test
    fun `GIVEN successful API response page 5 of 5 WHEN load THEN returns LoadResult Page with nextKey null and prevKey 4`() =
        runTest {
            val pagingSource = MoviePagingSource(mockSearchService, QUERY)
            val movieApiModel = movieApiModel.copy(id = 2, title = "Batman 5", overview = "Overview 5")
            val movie = movie.copy(id = 2, title = "Batman 5", overview = "Overview 5")
            val apiResponse =
                MovieSearchResponseApiModel(
                    page = 5,
                    results = listOf(movieApiModel),
                    totalPages = 5,
                    totalResults = 1,
                )

            whenever(
                mockSearchService.searchMovies(
                    query = any(),
                    page = any(),
                    includeAdult = any(),
                    language = any(),
                ),
            ).thenReturn(apiResponse.right())

            val result: PagingSource.LoadResult<Int, Movie> =
                pagingSource.load(
                    PagingSource.LoadParams.Append(
                        key = 5,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertEquals(
                PagingSource.LoadResult.Page(data = listOf(movie), prevKey = 4, nextKey = null),
                result,
            )
        }

    @Test
    fun `GIVEN API IOError WHEN load THEN returns LoadResult Error with Network`() =
        runTest {
            val pagingSource = MoviePagingSource(mockSearchService, QUERY)

            val error = IOError(IOException(NETWORK_ERROR_MESSAGE))
            whenever(mockSearchService.searchMovies(query = any(), page = any(), includeAdult = any(), language = any())).thenReturn(error.left())

            val result: PagingSource.LoadResult<Int, Movie> =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertMovieException(result) { it == MovieError.Network }
        }

    @Test
    fun `GIVEN API HttpError 401 WHEN load THEN returns LoadResult Error with Unauthorized`() =
        runTest {
            val pagingSource = MoviePagingSource(mockSearchService, QUERY)

            val error = HttpError(code = 401, message = UNAUTHORIZED_ERROR_MESSAGE, body = "{}")
            whenever(mockSearchService.searchMovies(query = any(), page = any(), includeAdult = any(), language = any())).thenReturn(error.left())

            val result: PagingSource.LoadResult<Int, Movie> =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertMovieException(result) { it == MovieError.Unauthorized }
        }

    @Test
    fun `GIVEN API HttpError 404 WHEN load THEN returns LoadResult Error with NotFound`() =
        runTest {
            val pagingSource = MoviePagingSource(mockSearchService, QUERY)

            val error = HttpError(code = 404, message = NOT_FOUND_ERROR_MESSAGE, body = "{}")
            whenever(mockSearchService.searchMovies(query = any(), page = any(), includeAdult = any(), language = any())).thenReturn(error.left())

            val result: PagingSource.LoadResult<Int, Movie> =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertMovieException(result) { it == MovieError.NotFound }
        }

    @Test
    fun `GIVEN API HttpError 500 WHEN load THEN returns LoadResult Error with Unknown`() =
        runTest {
            val pagingSource = MoviePagingSource(mockSearchService, QUERY)

            val error = HttpError(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, body = "{}")
            whenever(mockSearchService.searchMovies(query = any(), page = any(), includeAdult = any(), language = any())).thenReturn(error.left())

            val result: PagingSource.LoadResult<Int, Movie> =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertMovieException(result) { it is MovieError.Unknown && it.message == INTERNAL_SERVER_ERROR_MESSAGE }
        }

    @Test
    fun `GIVEN API UnexpectedCallError WHEN load THEN returns LoadResult Error with Unknown`() =
        runTest {
            val pagingSource = MoviePagingSource(mockSearchService, QUERY)

            val cause = Throwable(UNEXPECTED_FAILURE_MESSAGE)
            val error = UnexpectedCallError(cause)
            whenever(mockSearchService.searchMovies(query = any(), page = any(), includeAdult = any(), language = any())).thenReturn(error.left())

            val result: PagingSource.LoadResult<Int, Movie> =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertMovieException(result) {
                it is MovieError.Unknown && it.message == UNEXPECTED_FAILURE_MESSAGE
            }
        }

    private fun assertMovieException(
        result: PagingSource.LoadResult<Int, Movie>,
        expectedErrorPredicate: (MovieError) -> Boolean,
    ) {
        assertTrue(result is PagingSource.LoadResult.Error)
        val errorResult = result as PagingSource.LoadResult.Error
        assertTrue(errorResult.throwable is MovieException)
        val movieException = errorResult.throwable as MovieException
        assertTrue(expectedErrorPredicate(movieException.error))
    }

    private companion object {
        const val PAGE_SIZE = 50
        const val QUERY = "Batman"
        const val NETWORK_ERROR_MESSAGE = "Network failed"
        const val UNAUTHORIZED_ERROR_MESSAGE = "Unauthorized"
        const val NOT_FOUND_ERROR_MESSAGE = "Not Found"
        const val INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error"
        const val UNEXPECTED_FAILURE_MESSAGE = "Unexpected failure"
        val movieApiModel =
            MovieApiModel(
                id = 1,
                title = "Batman 1",
                overview = "Overview 1",
                posterPath = "/poster1.jpg",
                backdropPath = "/backdrop1.jpg",
                releaseDate = "2023-01-01",
                voteAverage = 8.5,
            )
        val movie =
            MovieMother(
                id = 1,
                title = "Batman 1",
                overview = "Overview 1",
                posterPath = "/poster1.jpg",
                backdropPath = "/backdrop1.jpg",
                releaseDate = "2023-01-01",
                voteAverage = 8.5,
            )
    }
}
