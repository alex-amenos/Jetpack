package com.alxnophis.jetpack.movies.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailEvent
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailState
import com.alxnophis.jetpack.movies.ui.contract.error
import com.alxnophis.jetpack.movies.ui.contract.isLoading
import com.alxnophis.jetpack.movies.ui.contract.movie
import com.alxnophis.jetpack.movies.ui.contract.movieId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class MovieDetailViewModel(
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle,
    initialState: MovieDetailState = MovieDetailState.initialState,
) : BaseViewModel<MovieDetailEvent, MovieDetailState>(initialState, savedStateHandle) {
    override val uiState: StateFlow<MovieDetailState> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initialState,
        )

    override fun sanitizeForSavedState(state: MovieDetailState): MovieDetailState = state.copy(isLoading = false)

    override fun handleEvent(event: MovieDetailEvent) {
        when (event) {
            is MovieDetailEvent.LoadMovie -> {
                loadMovieDetails(event.movieId)
            }

            MovieDetailEvent.ErrorDismissRequested -> {
                dismissError()
            }

            MovieDetailEvent.GoBackRequested -> {
                throw IllegalStateException("Go back not implemented in ViewModel")
            }
        }
    }

    private fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            if (currentUiState.movie != null) return@launch

            _uiState.updateCopy {
                MovieDetailState.isLoading set true
                MovieDetailState.movieId set movieId
                MovieDetailState.error set null
            }

            movieRepository
                .getMovieDetails(movieId)
                .fold(
                    { error ->
                        _uiState.updateCopy {
                            MovieDetailState.isLoading set false
                            MovieDetailState.error set error
                        }
                    },
                    { movieDetails ->
                        _uiState.updateCopy {
                            MovieDetailState.isLoading set false
                            MovieDetailState.movie set movieDetails
                        }
                    },
                )
        }
    }

    private fun dismissError() {
        _uiState.updateCopy {
            MovieDetailState.error set null
        }
    }
}
