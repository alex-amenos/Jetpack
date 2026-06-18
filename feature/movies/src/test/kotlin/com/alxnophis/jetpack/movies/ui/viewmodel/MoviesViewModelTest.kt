package com.alxnophis.jetpack.movies.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.alxnophis.jetpack.movies.data.repository.FakeMovieRepository
import com.alxnophis.jetpack.movies.ui.contract.MoviesEvent
import com.alxnophis.jetpack.movies.ui.contract.MoviesState
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest : BaseViewModelUnitTest() {
    private lateinit var repository: FakeMovieRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: MoviesViewModel

    override fun beforeEachCompleted() {
        repository = FakeMovieRepository()
        savedStateHandle = SavedStateHandle()
        viewModel = MoviesViewModel(repository, savedStateHandle)
    }

    @Test
    fun `GIVEN initial state WHEN initialized THEN state is Idle and paging flow defaults`() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertEquals(MoviesState.initialState, awaitItem())
            }
        }

    @Nested
    inner class OnSearchQueryChanged {
        @Test
        fun `GIVEN valid query WHEN dispatched THEN state updates query AND fetches PagingData after debounce`() =
            runTest(testDispatcher) {
                // We must collect the lazy Flow so the debounce/flatMapLatest operators run
                val job = launch { viewModel.moviesPagingFlow.collect {} }

                // GIVEN
                val query = "Batman"

                // WHEN
                viewModel.handleEvent(MoviesEvent.SearchQueryChanged(query))

                // THEN
                viewModel.uiState.test {
                    assertEquals(MoviesState(searchQuery = query), awaitItem())
                }

                // Advance time to pass the debounce delay
                advanceTimeBy(MoviesViewModel.SEARCH_DEBOUNCE_DELAY + 10)

                // Check that repository was eventually called
                assertEquals("Batman", repository.lastSearchQuery)
                assertEquals(1, repository.searchMoviesCallCount)

                job.cancel()
            }
    }

    @Nested
    inner class OnMovieClicked {
        @Test
        fun `GIVEN movie id WHEN dispatched THEN throws IllegalStateException`() {
            assertThrows(IllegalStateException::class.java) {
                viewModel.handleEvent(MoviesEvent.MovieClicked(123))
            }
        }
    }

    @Nested
    inner class OnGoBackRequested {
        @Test
        fun `GIVEN click WHEN dispatched THEN throws IllegalStateException`() {
            assertThrows(IllegalStateException::class.java) {
                viewModel.handleEvent(MoviesEvent.GoBackRequested)
            }
        }
    }
}
