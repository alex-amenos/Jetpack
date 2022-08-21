package com.alxnophis.jetpack.core.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.R
import com.alxnophis.jetpack.core.ui.theme.CoreTheme

@Composable
fun CoreTopBar(
    modifier: Modifier,
    title: String,
    onBack: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentPadding = PaddingValues(start = 12.dp)
    ) {
        IconButton(
            modifier = Modifier.testTag(CoreTags.TAG_CORE_BACK),
            onClick = onBack,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.core_cd_go_back),
                tint = MaterialTheme.colors.onPrimary,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = MaterialTheme.colors.onPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview
@Composable
private fun CoreTopBarPreview() {
    CoreTheme {
        CoreTopBar(
            modifier = Modifier.fillMaxWidth(),
            title = "Lorem ipsum",
            onBack = {}
        )
    }
}
