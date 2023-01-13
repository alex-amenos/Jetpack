package com.alxnophis.jetpack.myplayground.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.myplayground.R

@Composable
fun MyPlaygroundScreen(navController: NavController) {
    val navigateBack: () -> Unit = { navController.popBackStack() }
    BackHandler {
        navigateBack()
    }
    MyPlaygroundContent(navigateBack)
}

@Composable
internal fun MyPlaygroundContent(
    navigateBack: () -> Unit
) {
    AppTheme {
        AppTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.myplayground_title),
                    onBack = { navigateBack() },
                )
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(0.1f),
                    style = MaterialTheme.typography.h3,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondary,
                    textAlign = TextAlign.Center,
                    text = "Let's PLAY!",
                )
            }
        }
    }
}

@Preview
@Composable
private fun MyPlaygroundScreenPreview() {
    MyPlaygroundContent(
        navigateBack = {}
    )
}
