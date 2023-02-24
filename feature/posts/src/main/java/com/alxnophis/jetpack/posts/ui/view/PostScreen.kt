package com.alxnophis.jetpack.posts.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.composable.drawVerticalScrollbar
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.core.ui.theme.smallPadding
import com.alxnophis.jetpack.kotlin.constants.ZERO_FLOAT
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlin.math.roundToInt

@Composable
internal fun PostsScreen(
    viewModel: PostsViewModel,
    popBackStack: () -> Unit
) {
    BackHandler {
        popBackStack()
    }
    PostsContent(
        state = viewModel.uiState.collectAsState().value,
        handleEvent = viewModel::handleEvent,
        navigateBack = popBackStack
    )
}

/**
 * Nestedscroll
 * Link: https://developer.android.com/reference/kotlin/androidx/compose/ui/input/nestedscroll/package-summary
 */
@Composable
internal fun PostsContent(
    state: PostsState,
    handleEvent: PostsEvent.() -> Unit,
    navigateBack: () -> Unit
) {
    AppTheme {
        val toolbarHeight = 56.dp
        val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
        val toolbarOffsetHeightPx = remember { mutableStateOf(ZERO_FLOAT) }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val newOffset = toolbarOffsetHeightPx.value + delta
                    toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, ZERO_FLOAT)
                    return Offset.Zero
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
                .nestedScroll(nestedScrollConnection)
        ) {
            PostList(
                modifier = Modifier.fillMaxSize(),
                state = state,
                toolbarHeight = toolbarHeight,
                handleEvent = handleEvent
            )
            CoreTopBar(
                modifier = Modifier
                    .height(toolbarHeight)
                    .offset { IntOffset(x = ZERO_INT, y = toolbarOffsetHeightPx.value.roundToInt()) },
                title = stringResource(id = R.string.posts_title),
                onBack = { navigateBack() }
            )
            state.errorMessages.firstOrNull()?.let { error: ErrorMessage ->
                CoreErrorDialog(
                    errorMessage = error.composableMessage(),
                    dismissError = { handleEvent.invoke(PostsEvent.DismissError(error.id)) }
                )
            }
        }
    }
}

@Composable
internal fun PostList(
    modifier: Modifier,
    state: PostsState,
    toolbarHeight: Dp,
    handleEvent: PostsEvent.() -> Unit
) {
    val listState = rememberLazyListState()
    SwipeRefresh(
        modifier = modifier,
        indicatorPadding = PaddingValues(top = toolbarHeight),
        state = rememberSwipeRefreshState(state.isLoading),
        onRefresh = { handleEvent.invoke(PostsEvent.GetPosts) }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .drawVerticalScrollbar(listState),
            contentPadding = PaddingValues(top = toolbarHeight, start = mediumPadding, end = mediumPadding)
        ) {
            items(
                items = state.posts,
                key = { item: Post -> item.id },
                itemContent = { item: Post ->
                    CardPostItem(state, item)
                }
            )
        }
    }
}

@Composable
private fun CardPostItem(
    state: PostsState,
    item: Post
) {
    Card(
        elevation = 10.dp,
        modifier = Modifier
            .padding(vertical = mediumPadding)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(mediumPadding)
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .placeholder(
                        visible = state.isLoading,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    ),
                text = item.title.replaceFirstChar { it.uppercase() },
                color = MaterialTheme.colors.primary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = smallPadding, bottom = mediumPadding)
                    .placeholder(
                        visible = state.isLoading,
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    ),
                text = item.body.replaceFirstChar { it.uppercase() },
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostScreenPreview() {
    val post1 = Post(
        id = 1,
        userId = 1,
        title = "Title 1",
        body = stringResource(id = R.string.core_lorem_ipsum_m)
    )
    val post2 = Post(
        id = 2,
        userId = 1,
        title = "Title 2",
        body = stringResource(id = R.string.core_lorem_ipsum_xl)
    )
    val state = PostsState(
        isLoading = false,
        posts = listOf(post1, post2),
        errorMessages = emptyList()
    )
    PostsContent(
        state = state,
        handleEvent = {},
        navigateBack = {}
    )
}
