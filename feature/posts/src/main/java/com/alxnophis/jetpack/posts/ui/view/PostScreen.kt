package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreLoadingDialog
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.contract.PostsViewAction
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
internal fun PostsComposable(
    viewModel: PostsViewModel = getViewModel()
) {
    CoreTheme {
        val state = viewModel.uiState.collectAsState().value
        PostScreen(
            state,
            viewModel::setAction
        )
    }
}

@Composable
internal fun PostScreen(
    state: PostsState,
    onViewAction: (viewAction: PostsViewAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PostsTopBar(onViewAction)
            PostList(state)
            CoreLoadingDialog(isLoading = state.isLoading)
            state.errorMessages.firstOrNull()?.let { error: ErrorMessage ->
                CoreErrorDialog(
                    errorMessage = stringResource(error.messageId),
                    dismissError = { onViewAction.invoke(PostsViewAction.DismissError(error.id)) }
                )
            }
        }
    }
}

@Composable
internal fun PostsTopBar(
    onViewAction: (viewAction: PostsViewAction) -> Unit,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentPadding = PaddingValues(start = 12.dp)
    ) {
        IconButton(
            onClick = { onViewAction.invoke(PostsViewAction.Finish) }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.posts_cd_go_back),
                tint = MaterialTheme.colors.onPrimary,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = R.string.posts_title),
            color = MaterialTheme.colors.onPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
internal fun PostList(state: PostsState) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface),
    ) {
        items(
            items = state.posts,
            itemContent = { item: Post ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        text = item.title.replaceFirstChar { it.uppercase() },
                        color = MaterialTheme.colors.primary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp, bottom = 16.dp),
                        text = item.body.replaceFirstChar { it.uppercase() },
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light,
                    )
                    Divider(color = Color.LightGray)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostScreenPreview() {
    val post1 = Post(
        id = 1,
        userId = 1,
        title = "Title 1",
        body = stringResource(id = R.string.core_lorem_ipsum_m),
    )
    val post2 = Post(
        id = 2,
        userId = 1,
        title = "Title 2",
        body = stringResource(id = R.string.core_lorem_ipsum_xl),
    )
    val state = PostsState(
        isLoading = false,
        posts = listOf(post1, post2),
        errorMessages = listOf()
    )
    CoreTheme {
        PostScreen(
            state = state,
            onViewAction = {}
        )
    }
}
