package com.alxnophis.jetpack.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

internal val lightColorPalette = lightColors(
    primary = coreColorGreenRadioactive,
    primaryVariant = coreColorGreenRadioactiveDark,
    onPrimary = coreColorWhite,
    secondary = coreColorOrange,
    secondaryVariant = coreColorOrangeDark,
    onSecondary = coreColorVulcan,
)

@Composable
fun CoreTheme(
    content: @Composable () -> Unit
) {
    val colors = lightColorPalette
    MaterialTheme(
        colors = colors,
        content = content
    )
}
