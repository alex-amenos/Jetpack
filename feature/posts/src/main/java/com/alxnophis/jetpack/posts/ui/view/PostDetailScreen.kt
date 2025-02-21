package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.R
import com.alxnophis.jetpack.core.ui.composable.CoreTags
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.view.provider.PostDetailPreviewProvider

@Composable
internal fun PostDetailScreen(
    post: Post,
    onNavigateBack: () -> Unit = {},
) {
    AppTheme {
        Scaffold(
            topBar = {
                IconButton(
                    modifier =
                        Modifier
                            .safeDrawingPadding()
                            .testTag(CoreTags.TAG_CORE_BACK),
                    onClick = onNavigateBack,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.core_cd_close),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    text = post.titleCapitalized,
                    textAlign = TextAlign.Start,
                )
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    text = post.body,
                    textAlign = TextAlign.Justify,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun PostDetailPreview(
    @PreviewParameter(PostDetailPreviewProvider::class) post: Post,
) {
    PostDetailScreen(
        post = post,
        onNavigateBack = {},
    )
}
