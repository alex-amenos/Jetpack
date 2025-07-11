package com.alxnophis.jetpack.core.ui.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.alxnophis.jetpack.core.ui.theme.AppTheme

@Composable
@SuppressLint("ComposeModifierMissing")
fun CoreLoadingDialog(isLoading: Boolean) {
    if (isLoading) {
        Dialog(
            onDismissRequest = {},
            properties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                ),
        ) {
            CoreLoadingContent(
                modifier =
                    Modifier
                        .size(100.dp)
                        .background(
                            color = White,
                            shape = RoundedCornerShape(50.dp),
                        ),
            )
        }
    }
}

@Composable
fun CoreLoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(45.dp),
            strokeWidth = 5.dp,
        )
        CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            strokeWidth = 3.dp,
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 400)
@Composable
private fun CoreLoadingDialogPreview() {
    AppTheme {
        CoreLoadingDialog(isLoading = true)
    }
}
