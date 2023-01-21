package com.alxnophis.jetpack.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

internal val lightColorPalette = lightColors(
    primary = coreColorGreen,
    primaryVariant = coreColorGreenDark,
    onPrimary = coreColorWhite,
    secondary = coreColorOrange,
    secondaryVariant = coreColorOrangeDark,
    onSecondary = coreColorVulcan,
    error = coreColorRed,
    onError = coreColorWhite,
    surface = coreColorWhite,
    onSurface = coreColorCarbon,
    background = coreColorWhite,
    onBackground = coreColorCarbon,
)

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
