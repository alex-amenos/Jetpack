package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.posts.di.injectPosts
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun PostsFeature(
    onPostSelected: (Int) -> Unit,
    onBack: () -> Unit,
) {
    injectPosts()
    val viewModel = getViewModel<PostsViewModel>()
    PostsScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        handleEvent = { event ->
            when (event) {
                PostsEvent.GoBackRequested -> onBack()
                is PostsEvent.OnPostClicked -> onPostSelected(event.post.id)
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
