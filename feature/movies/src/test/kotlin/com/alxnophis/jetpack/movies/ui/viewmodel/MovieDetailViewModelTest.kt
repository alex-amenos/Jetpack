package com.alxnophis.jetpack.movies.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.movies.data.repository.FakeMovieRepository
import com.alxnophis.jetpack.movies.domain.model.MovieDetailsMother
import com.alxnophis.jetpack.movies.domain.model.MovieError
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailEvent
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailState
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
private class MovieDetailViewModelTest : BaseViewModelUnitTest() {
    private lateinit var repository: FakeMovieRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: MovieDetailViewModel

    override fun beforeEachCompleted() {
        repository = FakeMovieRepository()
        savedStateHandle = SavedStateHandle()
        viewModel = MovieDetailViewModel(repository, savedStateHandle)
    }

    @Nested
    inner class LoadMovie {
        @Test
        fun `GIVEN valid ID WHEN network succeeds THEN state transitions to Success`() =
            runTest(UnconfinedTestDispatcher(testScheduler)) {
                // GIVEN
                val movieDetails = MovieDetailsMother(id = 1, title = "Success Movie")
                repository.movieDetailsResult = movieDetails.right()

                viewModel.uiState.test {
                    assertEquals(MovieDetailState.initialState, awaitItem())

                    // WHEN
                    viewModel.handleEvent(MovieDetailEvent.LoadMovie(1))

                    // THEN
                    // With UnconfinedTestDispatcher, state transitions synchronously
                    // and StateFlow conflates the intermediate Loading state.
                    assertEquals(
                        MovieDetailState(
                            isLoading = false,
                            movieId = 1,
                            movie = movieDetails,
                            error = null,
                        ),
                        awaitItem(),
                    )
                }
                assertEquals(1, repository.getMovieDetailsCallCount)
            }

        @Test
        fun `GIVEN valid ID WHEN network fails THEN state transitions to Error`() =
            runTest(UnconfinedTestDispatcher(testScheduler)) {
                // GIVEN
                val error = MovieError.Network
                repository.movieDetailsResult = error.left()

                viewModel.uiState.test {
                    assertEquals(MovieDetailState.initialState, awaitItem())

                    // WHEN
                    viewModel.handleEvent(MovieDetailEvent.LoadMovie(1))

                    // THEN
                    assertEquals(
                        MovieDetailState(
                            isLoading = false,
                            movieId = 1,
                            movie = null,
                            error = error,
                        ),
                        awaitItem(),
                    )
                }
                assertEquals(1, repository.getMovieDetailsCallCount)
            }

        @Test
        fun `GIVEN valid ID WHEN already has movie THEN does not fetch again`() =
            runTest(UnconfinedTestDispatcher(testScheduler)) {
                // GIVEN
                val movieDetails = MovieDetailsMother(id = 1, title = "Success Movie")
                repository.movieDetailsResult = movieDetails.right()

                viewModel.uiState.test {
                    skipItems(1) // Initial state

                    viewModel.handleEvent(MovieDetailEvent.LoadMovie(1))

                    assertEquals(
                        MovieDetailState(isLoading = false, movieId = 1, movie = movieDetails, error = null),
                        awaitItem(),
                    )

                    assertEquals(1, repository.getMovieDetailsCallCount)

                    // WHEN
                    viewModel.handleEvent(MovieDetailEvent.LoadMovie(1))

                    // THEN
                    // Call count should not increase
                    assertEquals(1, repository.getMovieDetailsCallCount)
                }
            }
    }

    @Nested
    inner class ErrorDismissRequested {
        @Test
        fun `GIVEN error state WHEN dismissed THEN clears error from state`() =
            runTest(UnconfinedTestDispatcher(testScheduler)) {
                // GIVEN
                repository.movieDetailsResult = MovieError.NotFound.left()

                viewModel.uiState.test {
                    skipItems(1) // Initial state

                    viewModel.handleEvent(MovieDetailEvent.LoadMovie(1))

                    assertEquals(
                        MovieDetailState(isLoading = false, movieId = 1, movie = null, error = MovieError.NotFound),
                        awaitItem(),
                    )

                    // WHEN
                    viewModel.handleEvent(MovieDetailEvent.ErrorDismissRequested)

                    // THEN
                    assertEquals(
                        MovieDetailState(isLoading = false, movieId = 1, movie = null, error = null),
                        awaitItem(),
                    )
                }
            }
    }

    @Nested
    inner class GoBackRequested {
        @Test
        fun `GIVEN click WHEN dispatched THEN throws IllegalStateException`() {
            assertThrows(IllegalStateException::class.java) {
                viewModel.handleEvent(MovieDetailEvent.GoBackRequested)
            }
        }
    }
}
