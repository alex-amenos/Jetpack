package com.alxnophis.jetpack.core.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.smallPadding

@Preview(showBackground = true)
@Composable
private fun CoreColorsThemePreview() {
    AppTheme {
        val colorsMap: Map<String, Color> = mapOf(
            "primary" to MaterialTheme.colors.primary,
            "primaryVariant" to MaterialTheme.colors.primaryVariant,
            "onPrimary" to MaterialTheme.colors.onPrimary,
            "secondary" to MaterialTheme.colors.secondary,
            "secondaryVariant" to MaterialTheme.colors.secondaryVariant,
            "onSecondary" to MaterialTheme.colors.onSecondary,
            "background" to MaterialTheme.colors.background,
            "onBackground" to MaterialTheme.colors.onBackground,
            "surface" to MaterialTheme.colors.surface,
            "onSurface" to MaterialTheme.colors.onSurface,
            "error" to MaterialTheme.colors.error,
            "onError" to MaterialTheme.colors.onError
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            colorsMap.forEach { (colorName: String, color: Color) ->
                ColorItem(colorName, color)
            }
        }
    }
}

@Composable
private fun ColorItem(
    colorName: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .background(color = color)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = smallPadding),
            text = colorName
        )
    }
}
