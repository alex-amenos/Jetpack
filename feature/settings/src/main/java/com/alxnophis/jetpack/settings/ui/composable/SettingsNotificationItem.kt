package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.composable.SettingsTags.TAG_TOGGLE_ITEM

@Composable
internal fun SettingsNotificationItem(
    title: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onToggleNotificationSettings: () -> Unit,
) {
    val notificationsEnabledState =
        if (checked) {
            stringResource(id = R.string.settings_cd_notifications_enabled)
        } else {
            stringResource(id = R.string.settings_cd_notifications_disabled)
        }
    SettingsItem(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .toggleable(
                        value = checked,
                        onValueChange = { onToggleNotificationSettings() },
                        role = Role.Switch,
                    ).semantics { stateDescription = notificationsEnabledState }
                    .padding(mediumPadding)
                    .testTag(TAG_TOGGLE_ITEM),
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = checked,
                onCheckedChange = null,
            )
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsNotificationItemPreview() {
    AppTheme {
        SettingsNotificationItem(
            modifier = Modifier.fillMaxWidth(),
            title = "Lorem ipsum dolor sit amet",
            checked = true,
            onToggleNotificationSettings = {},
        )
    }
}
