package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
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
        uiState = state,
        movies = moviesPagingItems,
        onBack = onBack,
        onMovieClicked = onMovieSelected,
        handleEvent = viewModel::handleEvent,
    )
}
