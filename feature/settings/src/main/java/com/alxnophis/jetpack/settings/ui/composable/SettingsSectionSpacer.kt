package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun SettingsSectionSpacer(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .height(48.dp)
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                ),
    )
}
