package com.alxnophis.jetpack.settings.ui.view

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.alxnophis.jetpack.core.extensions.getVersion
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
internal fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = getViewModel(),
    appVersion: String = LocalContext.current.getVersion()
) {
    CoreTheme {
        val state = viewModel.uiState.collectAsState().value
        val navigateBack: () -> Unit = { navController.popBackStack() }
        BackHandler { navigateBack() }
        Settings(
            state = state,
            appVersion = appVersion,
            onSettingsEvent = viewModel::setEvent,
            onNavigateBack = navigateBack
        )
    }
}

@Composable
internal fun Settings(
    state: SettingsState,
    appVersion: String,
    onSettingsEvent: (event: SettingsEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .verticalScroll(rememberScrollState())
    ) {
        CoreTopBar(
            title = stringResource(id = R.string.settings_title),
            onBack = { onNavigateBack() }
        )
        Divider()
        SettingsNotificationItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(id = R.string.settings_option_notifications),
            checked = state.notificationsEnabled,
            onToggleNotificationSettings = { onSettingsEvent(SettingsEvent.SetNotifications) }
        )
        Divider()
        SettingsHintItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(id = R.string.settings_option_hints),
            checked = state.hintsEnabled,
            onShowHintToggled = { onSettingsEvent(SettingsEvent.SetHint) }
        )
        Divider()
        SettingsManageSubscriptionItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(id = R.string.settings_option_manage_subscription),
            onSubscriptionClicked = {
                Toast
                    .makeText(context, R.string.settings_option_manage_subscription, Toast.LENGTH_LONG)
                    .show()
                onSettingsEvent(SettingsEvent.ManageSubscription)
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
                onSettingsEvent(SettingsEvent.SetMarketingOption(marketingOption))
            }
        )
        Divider()
        SettingsThemeItem(
            modifier = Modifier.fillMaxWidth(),
            selectedTheme = state.themeOption,
            onOptionSelected = { theme ->
                onSettingsEvent(SettingsEvent.SetTheme(theme))
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

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsScreenPreview() {
    CoreTheme {
        Settings(
            state = SettingsState(),
            appVersion = "1.0.0",
            onSettingsEvent = {},
            onNavigateBack = {}
        )
    }
}
