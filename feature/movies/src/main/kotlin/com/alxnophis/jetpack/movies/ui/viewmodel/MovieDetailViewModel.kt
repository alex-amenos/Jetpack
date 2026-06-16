package com.alxnophis.jetpack.movies.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailEvent
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailState
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

            updateUiState {
                copy(
                    isLoading = true,
                    movieId = movieId,
                    error = null,
                )
            }

            movieRepository
                .getMovieDetails(movieId)
                .fold(
                    { error ->
                        updateUiState {
                            copy(
                                isLoading = false,
                                error = error,
                            )
                        }
                    },
                    { movieDetails ->
                        updateUiState {
                            copy(
                                isLoading = false,
                                movie = movieDetails,
                            )
                        }
                    },
                )
        }
    }

    private fun dismissError() {
        updateUiState {
            copy(error = null)
        }
    }
}
