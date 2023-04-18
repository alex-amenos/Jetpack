package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.settings.R

@Composable
internal fun SettingsAppVersion(
    appVersion: String,
    modifier: Modifier = Modifier
) {
    SettingsItem(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = mediumPadding)
                .semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.settings_app_version)
            )
            Text(text = appVersion)
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsAppVersionPreview() {
    AppTheme {
        SettingsAppVersion(
            modifier = Modifier.fillMaxWidth(),
            appVersion = "1.0.1"
        )
    }
}
