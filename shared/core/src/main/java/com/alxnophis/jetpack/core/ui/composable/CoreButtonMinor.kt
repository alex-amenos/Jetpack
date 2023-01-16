package com.alxnophis.jetpack.core.ui.composable

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.extraSmallPadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding

@SuppressLint("ModifierParameter")
@Composable
fun CoreButtonMinor(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean = true,
    onClick: (() -> Unit),
) {
    AppTheme {
        OutlinedButton(
            modifier = modifier,
            enabled = isEnabled,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (isEnabled) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled)
                }
            ),
            onClick = { onClick() },
        ) {
            Text(
                style = MaterialTheme.typography.button,
                color = if (isEnabled) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled)
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(extraSmallPadding),
                text = text,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoreButtonMinorPreview() {
    val context = LocalContext.current
    val clicked: (() -> Unit) = {
        Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show()
    }
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(mediumPadding),
    ) {
        CoreButtonMinor(
            modifier = Modifier.fillMaxWidth(),
            text = "Button",
            onClick = clicked,
        )

        Spacer(modifier = Modifier.height(50.dp))

        CoreButtonMinor(
            modifier = Modifier.fillMaxWidth(),
            text = "Button disabled",
            isEnabled = false,
            onClick = clicked,
        )
    }
}
