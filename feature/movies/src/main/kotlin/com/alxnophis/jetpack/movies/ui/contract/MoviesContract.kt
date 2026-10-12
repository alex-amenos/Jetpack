package com.alxnophis.jetpack.movies.ui.contract

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MoviesUiState(
    val searchQuery: String,
) : UiState,
    Parcelable {
    companion object {
        val initialState = MoviesUiState(searchQuery = EMPTY)
    }
}

sealed interface MoviesUiEvent : UiEvent {
    data class SearchQueryChanged(
        val query: String,
    ) : MoviesUiEvent
}
