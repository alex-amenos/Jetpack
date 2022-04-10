package com.alxnophis.jetpack.settings.ui.view

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.extensions.getVersion
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
internal fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
    appVersion: String = LocalContext.current.getVersion()
) {
    CoreTheme {
        val state = viewModel.uiState.collectAsState().value
        Settings(
            state = state,
            appVersion = appVersion,
            onSettingsEvent = viewModel::setEvent
        )
    }
}

@Composable
internal fun Settings(
    state: SettingsState,
    appVersion: String,
    onSettingsEvent: (event: SettingsEvent) -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsTopBar(onSettingsEvent)
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

@Composable
internal fun SettingsTopBar(
    onSettingsEvent: (event: SettingsEvent) -> Unit,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentPadding = PaddingValues(start = 12.dp)
    ) {
        IconButton(
            onClick = { onSettingsEvent.invoke(SettingsEvent.Finish) }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.settings_cd_go_back),
                tint = MaterialTheme.colors.onPrimary,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = R.string.settings_title),
            color = MaterialTheme.colors.onPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
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
            onSettingsEvent = {}
        )
    }
}
