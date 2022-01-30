package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption

@Composable
internal fun SettingsMarketingItem(
    modifier: Modifier,
    selectedOption: MarketingOption,
    onOptionSelected: (position: MarketingOption) -> Unit
) {
    val options = stringArrayResource(R.array.settings_options_marketing_choice)
    SettingsItem(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.settings_option_marketing),
            )
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = selectedOption.id == index,
                            onClick = {
                                val marketingOption = if (index == MarketingOption.ALLOWED.id) {
                                    MarketingOption.ALLOWED
                                } else {
                                    MarketingOption.NOT_ALLOWED
                                }
                                onOptionSelected(marketingOption)
                            },
                            role = Role.RadioButton,
                        )
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                ) {
                    RadioButton(
                        selected = selectedOption.id == index,
                        onClick = null
                    )
                    Text(
                        text = option,
                        modifier = Modifier.padding(start = 18.dp)
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
    CoreTheme {
        SettingsMarketingItem(
            modifier = Modifier.fillMaxWidth(),
            selectedOption = MarketingOption.NOT_ALLOWED,
            onOptionSelected = {}
        )
    }
}
