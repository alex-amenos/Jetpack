package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.largePadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.view.SettingsTags.TAG_MARKETING_OPTION

@Composable
internal fun SettingsMarketingItem(
    selectedOption: MarketingOption,
    modifier: Modifier = Modifier,
    onOptionSelected: (position: MarketingOption) -> Unit,
) {
    val options = stringArrayResource(R.array.settings_options_marketing_choice)
    SettingsItem(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                modifier = Modifier.padding(mediumPadding),
                text = stringResource(R.string.settings_option_marketing),
            )
            options.forEachIndexed { index, option ->
                Row(
                    modifier =
                        Modifier
                            .selectable(
                                selected = selectedOption.id == index,
                                onClick = {
                                    val marketingOption =
                                        if (index == MarketingOption.ALLOWED.id) {
                                            MarketingOption.ALLOWED
                                        } else {
                                            MarketingOption.NOT_ALLOWED
                                        }
                                    onOptionSelected(marketingOption)
                                },
                                role = Role.RadioButton,
                            )
                            .fillMaxWidth()
                            .padding(start = largePadding, end = mediumPadding, top = mediumPadding, bottom = mediumPadding)
                            .testTag(TAG_MARKETING_OPTION + index),
                ) {
                    RadioButton(
                        selected = selectedOption.id == index,
                        onClick = null,
                    )
                    Text(
                        text = option,
                        modifier = Modifier.padding(start = mediumPadding),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@ExperimentalComposeUiApi
@Composable
private fun SettingsMarketingItemPreview() {
    AppTheme {
        SettingsMarketingItem(
            modifier = Modifier.fillMaxWidth(),
            selectedOption = MarketingOption.NOT_ALLOWED,
            onOptionSelected = {},
        )
    }
}
