package com.alxnophis.jetpack.movies.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import com.alxnophis.jetpack.movies.ui.contract.MoviesEvent
import com.alxnophis.jetpack.movies.ui.contract.MoviesState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
internal class MoviesViewModel(
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle,
    initialState: MoviesState = MoviesState.initialState,
) : BaseViewModel<MoviesEvent, MoviesState>(initialState, savedStateHandle) {
    private val searchQueryFlow = MutableStateFlow(currentUiState.searchQuery)

    val moviesPagingFlow: Flow<PagingData<Movie>> =
        searchQueryFlow
            .debounce(SEARCH_DEBOUNCE_DELAY)
            .flatMapLatest { query -> movieRepository.searchMovies(query) }
            .cachedIn(viewModelScope)

    override fun handleEvent(event: MoviesEvent) {
        when (event) {
            is MoviesEvent.SearchQueryChanged -> {
                updateUiState {
                    copy(searchQuery = event.query)
                }
                searchQueryFlow.update {
                    event.query
                }
            }

            is MoviesEvent.MovieClicked -> {
                throw IllegalStateException("MovieClicked event should be handled in the UI layer, not in the ViewModel.")
            }

            MoviesEvent.GoBackRequested -> {
                throw IllegalStateException("Go back not implemented in ViewModel")
            }
        }
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 500L
    }
}
