package com.alxnophis.jetpack.core.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alxnophis.jetpack.core.R
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.extraSmallPadding
import com.alxnophis.jetpack.core.ui.theme.smallPadding

@Composable
fun CoreErrorDialog(
    errorMessage: String,
    modifier: Modifier = Modifier,
    dismissError: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = dismissError,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.core_error_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
            )
        },
        confirmButton = {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = extraSmallPadding, end = smallPadding),
                contentAlignment = Alignment.CenterEnd,
            ) {
                TextButton(
                    onClick = { dismissError() },
                ) {
                    Text(
                        text = stringResource(android.R.string.ok),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
    )
}

@PreviewLightDark
@Composable
private fun CoreErrorDialogLongMessagePreview() {
    AppTheme {
        CoreErrorDialog(
            errorMessage = "Unable to connect to the server. Please check your internet connection and try again. If the problem persists, contact support.",
            dismissError = {},
        )
    }
}
