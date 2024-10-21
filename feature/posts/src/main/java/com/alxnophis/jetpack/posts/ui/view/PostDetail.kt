package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.R
import com.alxnophis.jetpack.core.ui.composable.CoreTags
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.posts.data.model.Post

@Composable
internal fun PostDetail(
    post: Post,
    onNavigateBack: () -> Unit,
) {
    AppTheme {
        Scaffold(
            topBar = {
                IconButton(
                    modifier =
                        Modifier
                            .padding(top = 12.dp, start = 4.dp)
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
private fun PostDetailPreview() {
    val post =
        Post(
            id = 1,
            userId = 1,
            title = "Lorem ipsum dolor",
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        )
    PostDetail(
        post = post,
        onNavigateBack = {},
    )
}
