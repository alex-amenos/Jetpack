package com.alxnophis.jetpack.authentication.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.core.ui.theme.AppTheme

@Composable
internal fun AuthorizedScreen(
    userEmail: String,
    popBackStack: () -> Unit
) {
    BackHandler {
        popBackStack()
    }
    AuthorizedContent(userEmail)
}

@Composable
internal fun AuthorizedContent(
    userEmail: String
) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.authentication_authorized, userEmail),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthorizedScreenPreview() {
    AuthorizedContent(
        userEmail = "my@email.com"
    )
}
