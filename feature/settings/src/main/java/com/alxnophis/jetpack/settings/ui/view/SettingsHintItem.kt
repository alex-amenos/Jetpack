package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.settings.R

@Composable
fun SettingsHintItem(
    modifier: Modifier,
    title: String,
    checked: Boolean,
    onShowHintTootled: () -> Unit,
) {
    val hintsEnabledState = if (checked) {
        stringResource(id = R.string.settings_cd_hints_enabled)
    } else {
        stringResource(id = R.string.settings_cd_hints_disabled)
    }
    SettingsItem(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .toggleable(
                    value = checked,
                    onValueChange = { onShowHintTootled() },
                    role = Role.Checkbox
                )
                .semantics { stateDescription = hintsEnabledState }
                .padding(16.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
            )
            Checkbox(
                checked = checked,
                onCheckedChange = null,
            )
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsHintItemPreview() {
    CoreTheme {
        SettingsHintItem(
            modifier = Modifier.fillMaxWidth(),
            title = "Lorem ipsum dolor sit amet",
            checked = true,
            onShowHintTootled = {}
        )
    }
}
