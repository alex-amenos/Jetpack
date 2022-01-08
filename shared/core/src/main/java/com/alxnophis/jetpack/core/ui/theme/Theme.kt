package com.alxnophis.jetpack.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

private val lightColorPalette = lightColors(
    primary = coreColorBlue,
    primaryVariant = coreColorBlueDark,
    secondary = coreColorYellow,
    secondaryVariant = coreColorYellowDark,
    background = coreColorWhite,
    surface = coreColorWhite,
    error = coreColorRed,
    onPrimary = coreColorWhite,
    onSecondary = coreColorBlueDark,
    onBackground = coreColorVulcan,
    onSurface = coreColorVulcan,
    onError = coreColorWhite,
)

private val darkColorPalette = darkColors(
    primary = coreColorWhite,
    primaryVariant = coreColorWhiteSmoke,
    secondary = coreColorYellow,
    secondaryVariant = coreColorYellowDark,
    background = coreColorBlue,
    surface = coreColorBlue,
    error = coreColorRedVariant,
    onPrimary = coreColorBlueDark,
    onSecondary = coreColorBlueDark,
    onBackground = coreColorWhite,
    onSurface = coreColorWhite,
    onError = coreColorBlueDark,
)

private val lightTypography = Typography(
    h1 = Typography.h1.copy(color = coreColorBlueDark),
    h2 = Typography.h2.copy(color = coreColorBlueDark),
    h3 = Typography.h3.copy(color = coreColorBlueDark),
    h4 = Typography.h4.copy(color = coreColorBlueDark),
    h5 = Typography.h5.copy(color = coreColorBlueDark),
    h6 = Typography.h6.copy(color = coreColorBlueDark),
    subtitle1 = Typography.subtitle1.copy(color = coreColorBlueDark, fontWeight = FontWeight.SemiBold),
    subtitle2 = Typography.subtitle2.copy(color = coreColorGray70, fontWeight = FontWeight.Normal),
    body1 = Typography.body1.copy(color = coreColorVulcan),
    body2 = Typography.body2.copy(color = coreColorGray46),
    button = Typography.button.copy(color = coreColorWhite, fontWeight = FontWeight.Bold),
    caption = Typography.caption.copy(color = coreColorVulcan),
    overline = Typography.overline.copy(color = coreColorVulcan),
)

private val darkTypography = Typography(
    h1 = lightTypography.h1.copy(color = coreColorWhite),
    h2 = lightTypography.h2.copy(color = coreColorWhite),
    h3 = lightTypography.h3.copy(color = coreColorWhite),
    h4 = lightTypography.h4.copy(color = coreColorWhite),
    h5 = lightTypography.h5.copy(color = coreColorWhite),
    h6 = lightTypography.h6.copy(color = coreColorWhite),
    subtitle1 = lightTypography.subtitle1.copy(color = coreColorWhite),
    subtitle2 = lightTypography.subtitle2.copy(color = coreColorGray70),
    body1 = lightTypography.body1.copy(color = coreColorWhite),
    body2 = lightTypography.body2.copy(color = coreColorGray80),
    button = lightTypography.button.copy(color = coreColorWhite),
    caption = lightTypography.caption.copy(color = coreColorWhite),
    overline = lightTypography.overline.copy(color = coreColorWhite),
)

@Composable
fun CoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (darkTheme) {
        true -> darkColorPalette
        false -> lightColorPalette
    }

    val typography = when (darkTheme) {
        true -> darkTypography
        false -> lightTypography
    }

    MaterialTheme(
        colors = colors,
        shapes = Shapes,
        typography = typography,
        content = content
    )
}
