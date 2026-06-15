package com.alxnophis.jetpack.movies.ui.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import com.alxnophis.jetpack.movies.ui.contract.MoviesEvent
import com.alxnophis.jetpack.movies.ui.contract.MoviesState
import com.alxnophis.jetpack.movies.ui.contract.searchQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
internal class MoviesViewModel(
    private val movieRepository: MovieRepository,
    initialState: MoviesState = MoviesState.initialState,
) : BaseViewModel<MoviesEvent, MoviesState>(initialState) {
    private val searchQueryFlow = MutableStateFlow(initialState.searchQuery)

    val moviesPagingFlow: Flow<PagingData<Movie>> =
        searchQueryFlow
            .debounce(SEARCH_DEBOUNCE_DELAY)
            .flatMapLatest { query -> movieRepository.searchMovies(query) }
            .cachedIn(viewModelScope)

    override val uiState: StateFlow<MoviesState> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initialState,
        )

    override fun handleEvent(event: MoviesEvent) {
        when (event) {
            is MoviesEvent.SearchQueryChanged -> {
                _uiState.updateCopy {
                    MoviesState.searchQuery set event.query
                }
                searchQueryFlow.value = event.query
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
