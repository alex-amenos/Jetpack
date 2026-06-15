package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailEvent
import com.alxnophis.jetpack.movies.ui.viewmodel.MovieDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieDetailFeature(
    movieId: Int,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<MovieDetailViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.handleEvent(MovieDetailEvent.LoadMovie(movieId))
    }

    MovieDetailScreen(
        state = state,
        handleEvent = { event ->
            when (event) {
                MovieDetailEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
