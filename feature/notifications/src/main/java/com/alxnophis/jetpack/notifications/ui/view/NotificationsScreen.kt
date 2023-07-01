package com.alxnophis.jetpack.notifications.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.alxnophis.jetpack.core.base.provider.NotificationChannelProvider
import com.alxnophis.jetpack.core.extensions.showNotification
import com.alxnophis.jetpack.core.ui.composable.CoreButtonMajor
import com.alxnophis.jetpack.core.ui.composable.CoreButtonMinor
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.largePadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.notifications.R

@Composable
internal fun NotificationsScreen(
    navigateBack: () -> Unit = {}
) {
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
                    onBack = { navigateBack() }
                )
            }
        ) {
            NotificationPermission(
                modifier = Modifier
                    .padding(paddingValues = it)
                    .fillMaxSize()
                    .padding(mediumPadding)
            )
        }
    }
}

@Composable
private fun NotificationPermission(
    modifier: Modifier = Modifier
) {
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
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CoreButtonMinor(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(largePadding),
                text = stringResource(id = R.string.notifications_request_permission),
                onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
            )
            Divider(
                modifier = Modifier.height(15.dp),
                color = Color.Transparent
            )
        }
        CoreButtonMajor(
            modifier = Modifier
                .fillMaxWidth()
                .padding(largePadding),
            text = stringResource(id = R.string.notifications_show_notification),
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
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsContentPreview() {
    NotificationsScreen()
}
