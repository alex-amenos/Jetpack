package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.settings.R

@Composable
internal fun SettingsManageSubscriptionItem(
    modifier: Modifier = Modifier,
    title: String,
    onSubscriptionClicked: () -> Unit,
) {
    SettingsItem(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    onClickLabel = stringResource(id = R.string.settings_cd_open_subscription),
                ) {
                    onSubscriptionClicked()
                }
                .padding(16.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsManageSubscriptionItemPreview() {
    AppTheme {
        SettingsManageSubscriptionItem(
            modifier = Modifier.fillMaxWidth(),
            title = "Lorem ipsum dolor sit amet",
            onSubscriptionClicked = {}
        )
    }
}
