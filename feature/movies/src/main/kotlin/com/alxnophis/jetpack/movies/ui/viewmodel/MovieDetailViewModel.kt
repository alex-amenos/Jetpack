package com.alxnophis.jetpack.movies.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.movies.domain.repository.MovieRepository
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailEvent
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailState
import com.alxnophis.jetpack.movies.ui.contract.error
import com.alxnophis.jetpack.movies.ui.contract.isLoading
import com.alxnophis.jetpack.movies.ui.contract.movie
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class MovieDetailViewModel(
    private val movieRepository: MovieRepository,
    initialState: MovieDetailState = MovieDetailState.initialState,
) : BaseViewModel<MovieDetailEvent, MovieDetailState>(initialState) {
    override val uiState: StateFlow<MovieDetailState> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initialState,
        )

    private fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.updateCopy {
                MovieDetailState.isLoading set true
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

    override fun handleEvent(event: MovieDetailEvent) {
        when (event) {
            is MovieDetailEvent.LoadMovie -> {
                loadMovieDetails(event.movieId)
            }

            MovieDetailEvent.ErrorDismissRequested -> {
                _uiState.updateCopy {
                    MovieDetailState.error set null
                }
            }

            MovieDetailEvent.GoBackRequested -> {
                throw IllegalStateException("Go back not implemented in ViewModel")
            }
        }
    }
}
