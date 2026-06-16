package com.alxnophis.jetpack.movies.ui.contract

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.domain.model.MovieError
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MovieDetailState(
    val isLoading: Boolean,
    val movieId: Int?,
    val movie: MovieDetails?,
    val error: MovieError?,
) : UiState,
    Parcelable {
    companion object {
        val initialState =
            MovieDetailState(
                isLoading = true,
                movieId = null,
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
