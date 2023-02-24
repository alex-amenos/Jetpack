package com.alxnophis.jetpack.core.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.R
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.extraSmallPadding
import com.alxnophis.jetpack.core.ui.theme.smallPadding

@Composable
fun CoreErrorDialog(
    modifier: Modifier = Modifier,
    errorMessage: String,
    dismissError: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { dismissError() },
        buttons = {
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
                        text = stringResource(R.string.core_ok),
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(R.string.core_error_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(text = errorMessage)
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
