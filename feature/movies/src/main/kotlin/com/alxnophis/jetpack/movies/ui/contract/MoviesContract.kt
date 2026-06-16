package com.alxnophis.jetpack.movies.ui.contract

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MoviesState(
    val searchQuery: String,
) : UiState, Parcelable {
    companion object {
        val initialState = MoviesState(searchQuery = EMPTY)
    }
}

sealed interface MoviesEvent : UiEvent {
    data class SearchQueryChanged(
        val query: String,
    ) : MoviesEvent

    data class MovieClicked(
        val movieId: Int,
    ) : MoviesEvent

    data object GoBackRequested : MoviesEvent
}
