package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.composable.CoreLoadingContent
import com.alxnophis.jetpack.core.ui.composable.CoreTags
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.ui.composable.provider.PostDetailPreviewProvider
import com.alxnophis.jetpack.posts.ui.contract.PostDetailEvent
import com.alxnophis.jetpack.posts.ui.contract.PostDetailUiError
import com.alxnophis.jetpack.posts.ui.contract.PostDetailUiState

@Composable
internal fun PostDetailScreen(
    uiState: PostDetailUiState,
    handleEvent: (PostDetailEvent) -> Unit = {},
) {
    when {
        uiState.isLoading -> PostDetailLoading()
        uiState.isSuccess -> PostDetailContent(uiState, handleEvent)
        uiState.isError -> PostDetailUiErrors(uiState, handleEvent)
    }
}

@Composable
internal fun PostDetailLoading() {
    AppTheme {
        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            contentWindowInsets = WindowInsets.safeGestures,
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CoreLoadingContent(
                    modifier =
                        Modifier
                            .size(150.dp)
                            .background(
                                color = White,
                                shape = RoundedCornerShape(50.dp),
                            ),
                )
            }
        }
    }
}

@Composable
internal fun PostDetailUiErrors(
    uiState: PostDetailUiState,
    handleEvent: PostDetailEvent.() -> Unit,
) {
    uiState.error?.let {
        val errorMessage =
            when (uiState.error) {
                PostDetailUiError.Network -> stringResource(R.string.posts_error_network)
                PostDetailUiError.NotFound -> stringResource(R.string.posts_error_not_found)
                PostDetailUiError.Server -> stringResource(R.string.posts_error_server)
                PostDetailUiError.Unknown -> stringResource(R.string.posts_error_unknown)
                PostDetailUiError.Unexpected -> stringResource(R.string.posts_error_unexpected)
            }
        AppTheme {
            Scaffold(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                contentWindowInsets = WindowInsets.safeGestures,
            ) { paddingValues ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                ) {
                    IconButton(
                        modifier =
                            Modifier
                                .wrapContentWidth()
                                .align(Alignment.TopEnd)
                                .testTag(CoreTags.TAG_CORE_BACK),
                        onClick = { handleEvent(PostDetailEvent.GoBackRequested) },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(id = R.string.core_cd_close),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_error),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun PostDetailContent(
    uiState: PostDetailUiState,
    handleEvent: (PostDetailEvent) -> Unit = {},
) {
    AppTheme {
        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            contentWindowInsets = WindowInsets.safeGestures,
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
            ) {
                IconButton(
                    modifier =
                        Modifier
                            .wrapContentWidth()
                            .align(Alignment.End)
                            .testTag(CoreTags.TAG_CORE_BACK),
                    onClick = { handleEvent(PostDetailEvent.GoBackRequested) },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.core_cd_close),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    text = uiState.postTitle,
                    textAlign = TextAlign.Start,
                )
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    text = uiState.postBody,
                    textAlign = TextAlign.Justify,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(fontScale = 1f)
@Preview(fontScale = 1.5f)
@Preview(fontScale = 2f)
@Preview(widthDp = 640, heightDp = 360)
@Composable
internal fun PostDetailPreview(
    @PreviewParameter(PostDetailPreviewProvider::class) state: PostDetailUiState,
) {
    PostDetailScreen(
        uiState = state,
        handleEvent = {},
    )
}
