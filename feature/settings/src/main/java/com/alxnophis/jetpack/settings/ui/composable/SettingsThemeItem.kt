package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.Theme

@Composable
internal fun SettingsThemeItem(
    selectedTheme: Theme,
    modifier: Modifier = Modifier,
    onOptionSelected: (option: Theme) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    SettingsItem(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .clickable(
                        onClick = { expanded = !expanded },
                        onClickLabel = stringResource(R.string.settings_cd_select_theme),
                    ).padding(mediumPadding)
                    .testTag(SettingsTags.TAG_SELECT_THEME),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.settings_option_theme),
            )
            Text(
                modifier = Modifier.testTag(SettingsTags.TAG_THEME),
                text = stringResource(selectedTheme.label),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 16.dp, y = 0.dp),
            properties = PopupProperties(usePlatformDefaultWidth = true),
        ) {
            Theme.values().forEach { theme ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(theme)
                        expanded = false
                    },
                    modifier = Modifier.testTag(SettingsTags.TAG_THEME_OPTION + theme),
                    text = { Text(text = stringResource(theme.label)) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsThemeItemPreview() {
    AppTheme {
        SettingsThemeItem(
            modifier = Modifier.fillMaxWidth(),
            selectedTheme = Theme.SYSTEM,
            onOptionSelected = {},
        )
    }
}
