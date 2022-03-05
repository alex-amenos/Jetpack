package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.Theme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SettingsThemeItem(
    modifier: Modifier,
    selectedTheme: Theme,
    onOptionSelected: (option: Theme) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    SettingsItem(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable(
                    onClick = { expanded = !expanded },
                    onClickLabel = stringResource(R.string.settings_cd_select_theme)
                )
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.settings_option_theme)
            )
            Text(
                text = stringResource(selectedTheme.label)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 16.dp, y = 0.dp),
            properties = PopupProperties(usePlatformDefaultWidth = true)
        ) {
            Theme.values().forEach { theme ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(theme)
                        expanded = false
                    }
                ) {
                    Text(text = stringResource(theme.label))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsThemeItemPreview() {
    CoreTheme {
        SettingsThemeItem(
            modifier = Modifier.fillMaxWidth(),
            selectedTheme = Theme.SYSTEM,
            onOptionSelected = {}
        )
    }
}
