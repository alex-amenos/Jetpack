package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTags
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.composable.drawVerticalScrollbar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.composable.provider.PostsScreenPreviewProvider
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState

@Composable
internal fun PostsScreen(
    uiState: PostsUiState,
    onBack: () -> Unit = {},
    onPostSelected: (Long) -> Unit = {},
    handleEvent: PostsEvent.() -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    AppTheme {
        Scaffold(
            topBar = {
                CoreTopBar(
                    title = stringResource(id = R.string.posts_title),
                    onBack = onBack,
                )
            },
            snackbarHost = {
                val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(navigationBarsPadding),
                )
            },
            modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection()),
            contentWindowInsets = WindowInsets.statusBars,
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                uiState.error?.let { error: PostUiError ->
                    when (error) {
                        is PostUiError.NoConnectivity -> {
                            PostSnackbarError(
                                errorId = error.id,
                                errorMessage = stringResource(R.string.posts_error_no_connectivity),
                                snackbarHostState = snackbarHostState,
                                onDismiss = {
                                    PostsEvent
                                        .DismissErrorRequested(error.id)
                                        .handleEvent()
                                },
                            )
                        }

                        else -> {
                            PostDialogErrors(error, handleEvent)
                        }
                    }
                }
                PullToRefreshBox(
                    isRefreshing = uiState.isLoading,
                    onRefresh = {
                        PostsEvent.OnUpdatePostsRequested.handleEvent()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val lazyListState = rememberLazyListState()
                    PostList(
                        uiState = uiState,
                        onPostSelected = onPostSelected,
                        lazyListState = lazyListState,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .drawVerticalScrollbar(lazyListState)
                                .testTag(CoreTags.TAG_POSTS_LIST),
                    )
                }
            }
        }
    }
}

@Composable
private fun PostSnackbarError(
    errorId: Long,
    errorMessage: String,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(errorId) {
        val result =
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = null,
            )
        when (result) {
            SnackbarResult.Dismissed,
            SnackbarResult.ActionPerformed,
            -> onDismiss()
        }
    }
}

@Composable
private fun PostDialogErrors(
    error: PostUiError,
    handleEvent: PostsEvent.() -> Unit = {},
) {
    CoreErrorDialog(
        errorMessage =
            when (error) {
                is PostUiError.NoConnectivity -> stringResource(R.string.posts_error_no_connectivity)
                is PostUiError.Network -> stringResource(R.string.posts_error_network)
                is PostUiError.NotFound -> stringResource(R.string.posts_error_not_found)
                is PostUiError.Server -> stringResource(R.string.posts_error_server)
                is PostUiError.Unexpected -> stringResource(R.string.posts_error_unexpected)
            },
        dismissError = {
            PostsEvent
                .DismissErrorRequested(error.id)
                .handleEvent()
        },
    )
}

@Composable
private fun PostList(
    uiState: PostsUiState,
    onPostSelected: (Long) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding =
            PaddingValues(
                start =
                    WindowInsets.safeDrawing
                        .asPaddingValues()
                        .calculateStartPadding(LocalLayoutDirection.current) + mediumPadding,
                end = mediumPadding,
            ),
    ) {
        items(
            items = uiState.posts,
            key = { item: Post -> item.id },
            itemContent = { item: Post ->
                CardPostItem(
                    item = item,
                    modifier =
                        Modifier
                            .padding(vertical = mediumPadding)
                            .shadow(1.dp, shape = RoundedCornerShape(8.dp))
                            .clickable { onPostSelected(item.id) }
                            .testTag(CoreTags.TAG_POST_ITEM)
                            .fillParentMaxWidth(),
                )
            },
        )
    }
}

@Composable
private fun CardPostItem(
    item: Post,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(mediumPadding),
        ) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = item.titleCapitalized,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = mediumPadding, bottom = mediumPadding),
                text = item.body.replaceFirstChar { it.uppercase() },
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PostScreenPreview(
    @PreviewParameter(PostsScreenPreviewProvider::class) state: PostsUiState,
) {
    PostsScreen(state)
}
