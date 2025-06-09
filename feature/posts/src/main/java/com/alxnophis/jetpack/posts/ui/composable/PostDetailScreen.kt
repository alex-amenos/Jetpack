package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.Alignment
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
import com.alxnophis.jetpack.posts.ui.composable.provider.PostDetailPreviewProvider

@Composable
internal fun PostDetailScreen(
    postId: Int,
    onNavigateBack: () -> Unit = {},
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
                    onClick = onNavigateBack,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
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
                    text = postId.toString(),
                    textAlign = TextAlign.Start,
                )
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    text = postId.toString(),
                    textAlign = TextAlign.Justify,
                )
            }
        }
    }
}

//@Preview(fontScale = 1f)
//@Preview(fontScale = 1.5f)
//@Preview(fontScale = 2f)
//@Preview(widthDp = 640, heightDp = 360)
//@Composable
//internal fun PostDetailPreview(
//    @PreviewParameter(PostDetailPreviewProvider::class) post: Post,
//) {
//    PostDetailScreen(
//        postId = post,
//        onNavigateBack = {},
//    )
//}
