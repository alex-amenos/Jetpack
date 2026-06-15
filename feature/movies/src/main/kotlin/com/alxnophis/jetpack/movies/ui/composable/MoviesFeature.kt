package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.runtime.Composable

@Composable
fun MoviesFeature(onBack: () -> Unit) {
    // Inject viewmodel using koinViewModel<MoviesViewModel>()
    // Pending to handle events and state from the viewmodel
    MoviesScreen { onBack() }
}
