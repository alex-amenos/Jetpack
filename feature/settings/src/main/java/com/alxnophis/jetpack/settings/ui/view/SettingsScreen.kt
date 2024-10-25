package com.alxnophis.jetpack.settings.ui.view

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.core.extensions.getVersion
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit = {},
    appVersion: String = LocalContext.current.getVersion(),
) {
    BackHandler { onEvent(SettingsEvent.GoBackRequested) }
    AppTheme {
        val context = LocalContext.current
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState()),
        ) {
            CoreTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.settings_title),
                onBack = { onEvent(SettingsEvent.GoBackRequested) },
            )
            HorizontalDivider()
            SettingsNotificationItem(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.settings_option_notifications),
                checked = state.notificationsEnabled,
                onToggleNotificationSettings = { onEvent(SettingsEvent.SetNotifications) },
            )
            HorizontalDivider()
            SettingsHintItem(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.settings_option_hints),
                checked = state.hintsEnabled,
                onShowHintToggled = { onEvent(SettingsEvent.SetHint) },
            )
            HorizontalDivider()
            SettingsManageSubscriptionItem(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.settings_option_manage_subscription),
                onSubscriptionClicked = {
                    Toast
                        .makeText(context, R.string.settings_option_manage_subscription, Toast.LENGTH_LONG)
                        .show()
                    onEvent(SettingsEvent.ManageSubscription)
                },
            )
            HorizontalDivider()
            SettingsSectionSpacer(
                modifier = Modifier.fillMaxWidth(),
            )
            SettingsMarketingItem(
                modifier = Modifier.fillMaxWidth(),
                selectedOption = state.marketingOption,
                onOptionSelected = { marketingOption ->
                    onEvent(SettingsEvent.SetMarketingOption(marketingOption))
                },
            )
            HorizontalDivider()
            SettingsThemeItem(
                modifier = Modifier.fillMaxWidth(),
                selectedTheme = state.themeOption,
                onOptionSelected = { theme ->
                    onEvent(SettingsEvent.SetTheme(theme))
                },
            )
            SettingsSectionSpacer(
                modifier = Modifier.fillMaxWidth(),
            )
            SettingsAppVersion(
                modifier = Modifier.fillMaxWidth(),
                appVersion = appVersion,
            )
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsPreviewProvider::class) uiState: SettingsState,
) {
    SettingsScreen(
        state = uiState,
        appVersion = "1.0.0",
    )
}
