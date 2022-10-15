package com.alxnophis.jetpack.notifications.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.alxnophis.jetpack.core.base.provider.NotificationChannelProvider
import com.alxnophis.jetpack.core.extensions.showNotification
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.notifications.R

@Composable
internal fun NotificationsScreen(
    navController: NavController,
) {
    val navigateBack: () -> Unit = { navController.popBackStack() }
    BackHandler {
        navigateBack()
    }
    NotificationsContent(navigateBack)
}

@Composable
private fun NotificationsContent(
    navigateBack: () -> Unit
) {
    CoreTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CoreTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.notifications_title),
                onBack = { navigateBack() },
            )
            NotificationPermission()
        }
    }
}

@Composable
private fun NotificationPermission() {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasNotificationPermission = isGranted }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            ) {
                Text(text = "Request permission")
            }
        }
        Button(
            onClick = {
                if (hasNotificationPermission) {
                    context.showNotification(
                        title = "Lorem ipsum",
                        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        icon = R.drawable.ic_push_notification,
                        channelId = NotificationChannelProvider.DEFAULT_NOTIFICATION_CHANNEL_ID,
                        notificationId = 1
                    )
                }
            }
        ) {
            Text(text = "Show notification")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsContentPreview() {
    NotificationsContent(
        navigateBack = {}
    )
}