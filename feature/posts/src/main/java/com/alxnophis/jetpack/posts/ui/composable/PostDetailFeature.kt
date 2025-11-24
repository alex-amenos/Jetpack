package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.posts.ui.contract.PostDetailEvent
import com.alxnophis.jetpack.posts.ui.viewmodel.PostDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PostDetailFeature(
    postId: Int,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<PostDetailViewModel>()
    val handleEvent: PostDetailEvent.() -> Unit = viewModel::handleEvent
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        PostDetailEvent.LoadPost(postId).handleEvent()
    }
    PostDetailScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        handleEvent = { event ->
            when (event) {
                PostDetailEvent.GoBackRequested -> onBack()
                else -> event.handleEvent()
            }
        },
    )
}
