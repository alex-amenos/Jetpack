package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreLoadingDialog
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.posts.R
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PostsTopBar(onViewAction)
        CoreLoadingDialog(isLoading = state.isLoading)
        Text(
            text = state.posts.toString(),
            color = MaterialTheme.colors.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        )
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
