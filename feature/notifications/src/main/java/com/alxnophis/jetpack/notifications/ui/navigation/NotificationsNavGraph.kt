package com.alxnophis.jetpack.notifications.ui.navigation

import androidx.compose.runtime.Composable
import com.alxnophis.jetpack.notifications.di.injectNotifications
import com.alxnophis.jetpack.notifications.ui.view.NotificationsScreen

@Composable
fun NotificationsFeature(
    onBack: () -> Unit,
) {
    injectNotifications()
    NotificationsScreen(
        navigateBack = onBack,
    )
}
