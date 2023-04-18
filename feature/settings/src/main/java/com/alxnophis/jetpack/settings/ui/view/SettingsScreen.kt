package com.alxnophis.jetpack.settings.ui.view

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.core.extensions.getVersion
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel

@Composable
internal fun SettingsScreen(
    viewModel: SettingsViewModel,
    popBackStack: () -> Unit,
    appVersion: String = LocalContext.current.getVersion()
) {
    val state = viewModel.uiState.collectAsState().value
    BackHandler { popBackStack() }
    SettingsContent(
        state = state,
        appVersion = appVersion,
        handleEvent = viewModel::handleEvent,
        navigateBack = popBackStack
    )
}

@Composable
internal fun SettingsContent(
    state: SettingsState,
    appVersion: String,
    handleEvent: SettingsEvent.() -> Unit,
    navigateBack: () -> Unit
) {
    AppTheme {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
        ) {
            CoreTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.settings_title),
                onBack = { navigateBack() }
            )
            Divider()
            SettingsNotificationItem(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.settings_option_notifications),
                checked = state.notificationsEnabled,
                onToggleNotificationSettings = { handleEvent(SettingsEvent.SetNotifications) }
            )
            Divider()
            SettingsHintItem(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.settings_option_hints),
                checked = state.hintsEnabled,
                onShowHintToggled = { handleEvent(SettingsEvent.SetHint) }
            )
            Divider()
            SettingsManageSubscriptionItem(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.settings_option_manage_subscription),
                onSubscriptionClicked = {
                    Toast
                        .makeText(context, R.string.settings_option_manage_subscription, Toast.LENGTH_LONG)
                        .show()
                    handleEvent(SettingsEvent.ManageSubscription)
                }
            )
            Divider()
            SettingsSectionSpacer(
                modifier = Modifier.fillMaxWidth()
            )
            SettingsMarketingItem(
                modifier = Modifier.fillMaxWidth(),
                selectedOption = state.marketingOption,
                onOptionSelected = { marketingOption ->
                    handleEvent(SettingsEvent.SetMarketingOption(marketingOption))
                }
            )
            Divider()
            SettingsThemeItem(
                modifier = Modifier.fillMaxWidth(),
                selectedTheme = state.themeOption,
                onOptionSelected = { theme ->
                    handleEvent(SettingsEvent.SetTheme(theme))
                }
            )
            SettingsSectionSpacer(
                modifier = Modifier.fillMaxWidth()
            )
            SettingsAppVersion(
                modifier = Modifier.fillMaxWidth(),
                appVersion = appVersion
            )
            Divider()
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsScreenPreview() {
    SettingsContent(
        state = SettingsState(),
        appVersion = "1.0.0",
        handleEvent = {},
        navigateBack = {}
    )
}
