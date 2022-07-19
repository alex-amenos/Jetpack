package com.alxnophis.jetpack.spacex.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.spacex.R

@Composable
internal fun SpacexScreen(
    navController: NavController
) {
    val navigateBack: () -> Unit = { navController.popBackStack() }
    BackHandler { navigateBack() }
    SpacexContent(
        onNavigateBack = navigateBack
    )
}

@Composable
internal fun SpacexContent(
    onNavigateBack: () -> Unit
) {
    CoreTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
        ) {
            CoreTopBar(
                title = stringResource(id = R.string.spacex_title),
                onBack = { onNavigateBack() }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lorem ipsum dolor sit amet",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpacexContentPreview() {
    SpacexContent(
        onNavigateBack = {}
    )
}
