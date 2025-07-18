package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.composable.drawVerticalScrollbar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.composable.provider.PostStatePreviewProvider
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState

@Composable
internal fun PostsScreen(
    state: PostsUiState,
    handleEvent: (PostsEvent) -> Unit = {},
) {
    PostContent(state, handleEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostContent(
    uiState: PostsUiState,
    handleEvent: PostsEvent.() -> Unit = {},
) {
    AppTheme {
        Scaffold(
            topBar = {
                CoreTopBar(
                    title = stringResource(id = R.string.posts_title),
                    onBack = { PostsEvent.GoBackRequested.handleEvent() },
                )
            },
            modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection()),
            contentWindowInsets = WindowInsets.statusBars,
        ) { padding ->
            uiState.error?.let { error: PostUiError ->
                CoreErrorDialog(
                    errorMessage =
                        when (error) {
                            PostUiError.Network -> stringResource(R.string.posts_error_network)
                            PostUiError.NotFound -> stringResource(R.string.posts_error_not_found)
                            PostUiError.Server -> stringResource(R.string.posts_error_server)
                            PostUiError.Unknown -> stringResource(R.string.posts_error_unknown)
                            PostUiError.Unexpected -> stringResource(R.string.posts_error_unexpected)
                        },
                    dismissError = { PostsEvent.DismissErrorRequested.handleEvent() },
                )
            }
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = {
                    PostsEvent.OnUpdatePostsRequested.handleEvent()
                },
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxWidth(),
            ) {
                val lazyListState = rememberLazyListState()
                PostList(
                    uiState = uiState,
                    handleEvent = handleEvent,
                    lazyListState = lazyListState,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .drawVerticalScrollbar(lazyListState),
                )
            }
        }
    }
}

@Composable
private fun PostList(
    uiState: PostsUiState,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    handleEvent: PostsEvent.() -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding =
            PaddingValues(
                start = WindowInsets.safeDrawing.asPaddingValues().calculateStartPadding(LocalLayoutDirection.current) + mediumPadding,
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
                            .clickable { PostsEvent.OnPostClicked(item).handleEvent() }
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
                modifier =
                    Modifier
                        .wrapContentSize(),
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

@Preview(showBackground = true)
@Composable
private fun PostScreenPreview(
    @PreviewParameter(PostStatePreviewProvider::class) state: PostsUiState,
) {
    PostsScreen(state)
}
