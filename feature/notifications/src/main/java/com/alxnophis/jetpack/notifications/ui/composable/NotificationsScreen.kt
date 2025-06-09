package com.alxnophis.jetpack.notifications.ui.composable

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.alxnophis.jetpack.core.base.provider.NotificationChannelProvider
import com.alxnophis.jetpack.core.extensions.appPendingIntent
import com.alxnophis.jetpack.core.extensions.showNotification
import com.alxnophis.jetpack.core.ui.composable.CoreButtonMajor
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.largePadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.notifications.R

@Composable
internal fun NotificationsScreen(navigateBack: () -> Unit = {}) {
    BackHandler {
        navigateBack()
    }
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.notifications_title),
                    onBack = { navigateBack() },
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) {
            NotificationPermission(
                modifier =
                    Modifier
                        .padding(paddingValues = it)
                        .fillMaxSize()
                        .padding(mediumPadding),
            )
        }
    }
}

@Composable
private fun NotificationPermission(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        } else {
            mutableStateOf(true)
        }
    }
    val showMissingNotificationPermission: () -> Unit = {
        Toast.makeText(context, R.string.notifications_missing_permissions, Toast.LENGTH_SHORT).show()
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
                if (isGranted.not()) {
                    showMissingNotificationPermission()
                }
            },
        )
    val requestPermission: () -> Unit = {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    LaunchedEffect(Unit) {
        requestPermission()
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CoreButtonMajor(
            modifier =
                Modifier
                    .widthIn(min = 300.dp)
                    .padding(largePadding),
            text = stringResource(id = R.string.notifications_show_notification),
            onClick = {
                if (hasNotificationPermission) {
                    context.showNotification(
                        titleResId = R.string.notifications_push_title,
                        contentResId = R.string.notifications_push_subtitle,
                        icon = R.drawable.notifications_ic_push,
                        channelId = NotificationChannelProvider.DEFAULT_NOTIFICATION_CHANNEL_ID,
                        notificationId = 1,
                        contentIntent = context.appPendingIntent(),
                    )
                } else {
                    showMissingNotificationPermission()
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsContentPreview() {
    NotificationsScreen()
}
