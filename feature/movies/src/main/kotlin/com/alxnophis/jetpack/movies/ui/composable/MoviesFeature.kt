package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.alxnophis.jetpack.movies.ui.contract.MoviesEvent
import com.alxnophis.jetpack.movies.ui.viewmodel.MoviesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoviesFeature(
    onMovieSelected: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<MoviesViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val moviesPagingItems = viewModel.moviesPagingFlow.collectAsLazyPagingItems()

    MoviesScreen(
        state = state,
        movies = moviesPagingItems,
        handleEvent = { event ->
            when (event) {
                is MoviesEvent.MovieClicked -> onMovieSelected(event.movieId)
                MoviesEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        }
    )
}
