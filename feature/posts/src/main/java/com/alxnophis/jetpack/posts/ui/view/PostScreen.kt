package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.composable.drawVerticalScrollbar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.kotlin.constants.ZERO_FLOAT
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.contract.UiPostError
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlin.math.roundToInt

private val toolbarHeight = 56.dp

/**
 * How Nested Scroll is used in Jetpack Compose:
 * Link: https://developer.android.com/reference/kotlin/androidx/compose/ui/input/nestedscroll/package-summary
 */
@Composable
internal fun PostsScreen(
    state: PostsState,
    onEvent: (PostsEvent) -> Unit = {},
) {
    PostContent(state, onEvent)
}

@Composable
private fun PostContent(
    state: PostsState,
    onEvent: (PostsEvent) -> Unit = {},
) {
    AppTheme {
        val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
        val toolbarOffsetHeightPx = remember { mutableFloatStateOf(ZERO_FLOAT) }
        val nestedScrollConnection =
            remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset {
                        val delta = available.y
                        val newOffset = toolbarOffsetHeightPx.floatValue + delta
                        toolbarOffsetHeightPx.floatValue = newOffset.coerceIn(-toolbarHeightPx, ZERO_FLOAT)
                        return Offset.Zero
                    }
                }
            }
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .nestedScroll(nestedScrollConnection),
        ) {
            PostList(
                state = state,
                toolbarHeight = toolbarHeight,
                handleEvent = onEvent,
                modifier = Modifier.fillMaxSize(),
            )
            CoreTopBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = ZERO_INT, y = toolbarOffsetHeightPx.floatValue.roundToInt()) },
                title = stringResource(id = R.string.posts_title),
                onBack = { onEvent(PostsEvent.GoBackRequested) },
            )
            state.error?.let { error: UiPostError ->
                CoreErrorDialog(
                    errorMessage =
                        when (error) {
                            UiPostError.Network -> stringResource(R.string.posts_error_network)
                            UiPostError.Server -> stringResource(R.string.posts_error_server)
                            UiPostError.Unknown -> stringResource(R.string.posts_error_unknown)
                            UiPostError.Unexpected -> stringResource(R.string.posts_error_unexpected)
                        },
                    dismissError = { onEvent.invoke(PostsEvent.DismissErrorRequested) },
                )
            }
        }
    }
}

@Composable
internal fun PostList(
    state: PostsState,
    toolbarHeight: Dp,
    modifier: Modifier = Modifier,
    handleEvent: PostsEvent.() -> Unit,
) {
    val listState = rememberLazyListState()
    SwipeRefresh(
        modifier = modifier,
        indicatorPadding = PaddingValues(top = toolbarHeight + 8.dp),
        state = rememberSwipeRefreshState(state.isLoading),
        onRefresh = { handleEvent.invoke(PostsEvent.OnUpdatePostsRequested) },
    ) {
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .drawVerticalScrollbar(listState),
            contentPadding = PaddingValues(top = toolbarHeight, start = mediumPadding, end = mediumPadding),
        ) {
            items(
                items = state.posts,
                key = { item: Post -> item.id },
                itemContent = { item: Post ->
                    CardPostItem(
                        state = state,
                        item = item,
                        modifier =
                            Modifier
                                .padding(vertical = mediumPadding)
                                .clickable { handleEvent.invoke(PostsEvent.OnPostClicked(item)) }
                                .fillParentMaxWidth(),
                    )
                },
            )
        }
    }
}

@Composable
private fun CardPostItem(
    state: PostsState,
    item: Post,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
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
                        .wrapContentSize()
                        .placeholder(
                            visible = state.isLoading,
                            color = Color.Gray,
                            shape = RoundedCornerShape(4.dp),
                        ),
                text = item.titleCapitalized,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = mediumPadding, bottom = mediumPadding)
                        .placeholder(
                            visible = state.isLoading,
                            color = Color.Gray,
                            shape = RoundedCornerShape(4.dp),
                        ),
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
    @PreviewParameter(PostStateProvider::class) state: PostsState,
) {
    PostsScreen(state)
}

private class PostStateProvider : PreviewParameterProvider<PostsState> {
    override val values =
        sequenceOf(
            PostsState(
                isLoading = false,
                posts = listOf(post1, post2),
                error = null,
            ),
            PostsState(
                isLoading = false,
                posts = emptyList(),
                error = UiPostError.Network,
            ),
            PostsState(
                isLoading = false,
                posts = emptyList(),
                error = UiPostError.Server,
            ),
            PostsState(
                isLoading = false,
                posts = emptyList(),
                error = UiPostError.Unknown,
            ),
            PostsState(
                isLoading = false,
                posts = emptyList(),
                error = UiPostError.Unexpected,
            ),
            PostsState(
                isLoading = true,
                posts = emptyList(),
                error = null,
            ),
        )

    companion object {
        val post1 =
            Post(
                id = 1,
                userId = 1,
                title = "Title 1",
                body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            )
        val post2 =
            Post(
                id = 2,
                userId = 1,
                title = "Title 2",
                body =
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            )
    }
}
