package com.alxnophis.jetpack.myplayground.ui.view

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.myplayground.R

@Composable
fun MyPlaygroundScreen(
    popBackStack: () -> Unit
) {
    BackHandler {
        popBackStack()
    }
    MyPlaygroundScaffold(navigateBack = popBackStack)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun MyPlaygroundScaffold(
    navigateBack: () -> Unit
) {
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = rememberScaffoldState(),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.myplayground_title),
                    onBack = { navigateBack() }
                )
            }
        ) { _ -> MyPlaygroundContent() }
    }
}

@Composable
internal fun MyPlaygroundContent() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .fillMaxSize()
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.wrapContentHeight(),
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Center,
            text = stringResource(R.string.myplayground_quote)
        )
    }
}

@Preview
@Composable
private fun MyPlaygroundScaffoldPreview() {
    MyPlaygroundScaffold(
        navigateBack = {}
    )
}
