package com.alxnophis.jetpack.core.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.R
import com.alxnophis.jetpack.core.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        colors =
        TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier.testTag(CoreTags.TAG_CORE_BACK),
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.core_cd_go_back),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@Preview
@Composable
private fun CoreTopBarPreview() {
    AppTheme {
        CoreTopBar(
            modifier = Modifier.fillMaxWidth(),
            title = "Lorem ipsum",
            onBack = {}
        )
    }
}
