package com.alxnophis.jetpack.notifications.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
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
                .background(MaterialTheme.colors.surface)
        ) {
            CoreTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.notifications_title),
                onBack = { navigateBack() },
            )
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
