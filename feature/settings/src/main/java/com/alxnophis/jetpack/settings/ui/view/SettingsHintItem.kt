package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
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
import com.alxnophis.jetpack.settings.ui.view.SettingsTags.TAG_CHECK_ITEM

@Composable
fun SettingsHintItem(
    title: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onShowHintToggled: () -> Unit,
) {
    val hintsEnabledState =
        if (checked) {
            stringResource(id = R.string.settings_cd_hints_enabled)
        } else {
            stringResource(id = R.string.settings_cd_hints_disabled)
        }
    SettingsItem(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .toggleable(
                        value = checked,
                        onValueChange = { onShowHintToggled() },
                        role = Role.Checkbox,
                    ).semantics { stateDescription = hintsEnabledState }
                    .padding(mediumPadding)
                    .testTag(TAG_CHECK_ITEM),
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
    AppTheme {
        SettingsHintItem(
            modifier = Modifier.fillMaxWidth(),
            title = "Lorem ipsum dolor sit amet",
            checked = true,
            onShowHintToggled = {},
        )
    }
}
