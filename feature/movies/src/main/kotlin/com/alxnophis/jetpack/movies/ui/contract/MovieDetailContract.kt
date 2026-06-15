package com.alxnophis.jetpack.movies.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.domain.model.MovieError

@optics
data class MovieDetailState(
    val isLoading: Boolean,
    val movie: MovieDetails?,
    val error: MovieError?,
) : UiState {
    companion object {
        val initialState =
            MovieDetailState(
                isLoading = true,
                movie = null,
                error = null,
            )
    }
}

sealed interface MovieDetailEvent : UiEvent {
    data class LoadMovie(
        val movieId: Int,
    ) : MovieDetailEvent

    data object GoBackRequested : MovieDetailEvent

    data object ErrorDismissRequested : MovieDetailEvent
}
