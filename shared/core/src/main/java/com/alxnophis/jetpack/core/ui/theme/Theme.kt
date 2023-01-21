package com.alxnophis.jetpack.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colors = lightColorPalette
    MaterialTheme(
        colors = colors,
        content = content
    )
}
