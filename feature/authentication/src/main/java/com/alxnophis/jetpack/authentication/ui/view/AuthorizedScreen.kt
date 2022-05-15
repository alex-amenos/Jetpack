package com.alxnophis.jetpack.authentication.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.core.ui.theme.CoreTheme

@Composable
internal fun AuthorizedScreen(
    navController: NavController
) {
    CoreTheme {
        Authorized()
    }
    BackHandler {
        navController.popBackStack()
    }
}

@Composable
internal fun Authorized() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface),
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
    AuthorizedScreen(
        navController = rememberNavController()
    )
}
