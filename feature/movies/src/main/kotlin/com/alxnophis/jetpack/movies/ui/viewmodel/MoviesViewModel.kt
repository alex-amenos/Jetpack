package com.alxnophis.jetpack.movies.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import com.alxnophis.jetpack.movies.ui.contract.MoviesUiEvent
import com.alxnophis.jetpack.movies.ui.contract.MoviesUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
internal class MoviesViewModel(
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle,
    initialState: MoviesUiState = MoviesUiState.initialState,
) : BaseViewModel<MoviesUiEvent, MoviesUiState>(initialState, savedStateHandle) {
    private val searchQueryFlow = MutableStateFlow(currentUiState.searchQuery)

    val moviesPagingFlow: Flow<PagingData<Movie>> =
        searchQueryFlow
            .debounce(SEARCH_DEBOUNCE_DELAY)
            .flatMapLatest { query -> movieRepository.searchMovies(query) }
            .cachedIn(viewModelScope)

    override fun handleEvent(event: MoviesUiEvent) {
        when (event) {
            is MoviesUiEvent.SearchQueryChanged -> {
                updateUiState {
                    copy(searchQuery = event.query)
                }
                searchQueryFlow.update {
                    event.query
                }
            }
        }
    }

    companion object {
        val SEARCH_DEBOUNCE_DELAY = 500.milliseconds
    }
}
