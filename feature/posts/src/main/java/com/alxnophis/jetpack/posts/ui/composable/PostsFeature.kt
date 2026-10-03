package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PostsFeature(
    onPostSelected: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<PostsViewModel>()
    PostsScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onBack = onBack,
        onPostSelected = onPostSelected,
        handleEvent = viewModel::handleEvent,
    )
}
