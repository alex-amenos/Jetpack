package com.alxnophis.jetpack.core.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.core.R
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.extraSmallPadding
import com.alxnophis.jetpack.core.ui.theme.smallPadding

@Composable
fun CoreErrorDialog(
    errorMessage: String,
    modifier: Modifier = Modifier,
    dismissError: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { dismissError() },
        title = {
            Text(
                text = stringResource(R.string.core_error_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        text = {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = extraSmallPadding, end = smallPadding),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = { dismissError() }
                ) {
                    Text(
                        text = stringResource(android.R.string.ok),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CoreErrorDialogPreview() {
    AppTheme {
        CoreErrorDialog(
            modifier = Modifier.wrapContentSize(),
            errorMessage = "Error message",
            dismissError = {}
        )
    }
}
