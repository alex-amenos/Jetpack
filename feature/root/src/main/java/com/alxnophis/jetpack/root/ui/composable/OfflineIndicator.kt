package com.alxnophis.jetpack.root.ui.composable

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.LocalDarkTheme
import com.alxnophis.jetpack.root.R

private val OfflineBackgroundLight = Color(0xFF004D33)
private val OfflineBackgroundDark = Color(0xFF003822)
private val OfflineTextColor = Color(0xFFFFFFFF)

@Composable
internal fun OfflineIndicator(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val isDarkTheme = LocalDarkTheme.current ?: isSystemInDarkTheme()
    if (!view.isInEditMode) {
        DisposableEffect(isOnline, isDarkTheme) {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                if (!isOnline) {
                    insetsController.isAppearanceLightStatusBars = false
                } else {
                    insetsController.isAppearanceLightStatusBars = !isDarkTheme
                }
            }
            onDispose {
                (view.context as? Activity)?.window?.let { window ->
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
                }
            }
        }

        if (!isOnline) {
            SideEffect {
                (view.context as? Activity)?.window?.let { window ->
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                }
            }
        }
    }

    AnimatedVisibility(
        visible = !isOnline,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier,
    ) {
        val backgroundColor = if (isDarkTheme) OfflineBackgroundDark else OfflineBackgroundLight
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .statusBarsPadding()
                    .padding(vertical = 4.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(id = R.string.root_offline_message),
                color = OfflineTextColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun OfflineIndicatorPreview() {
    AppTheme {
        OfflineIndicator(
            isOnline = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
