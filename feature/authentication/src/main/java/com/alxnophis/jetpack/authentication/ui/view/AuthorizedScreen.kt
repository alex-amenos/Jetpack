package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.core.ui.theme.CoreTheme

@Composable
internal fun AuthorizedScreen() {
    CoreTheme {
        Authorized()
    }
}

@Composable
internal fun Authorized() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.authentication_authorized),
            style = MaterialTheme.typography.h6,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthorizedScreenPreview() {
    AuthorizedScreen()
}
