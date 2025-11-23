package com.alxnophis.jetpack.notifications.ui.navigation

import androidx.compose.runtime.Composable
import com.alxnophis.jetpack.notifications.ui.composable.NotificationsScreen

@Composable
fun NotificationsFeature(onBack: () -> Unit) {
    NotificationsScreen(
        navigateBack = onBack,
    )
}
