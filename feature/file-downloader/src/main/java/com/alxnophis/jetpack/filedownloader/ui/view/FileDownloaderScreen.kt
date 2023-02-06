package com.alxnophis.jetpack.filedownloader.ui.view

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.filedownloader.R

@Composable
fun FileDownloaderScreen(
    popBackStack: () -> Unit,
) {
    BackHandler {
        popBackStack()
    }
    FileDownloaderScaffold(
        navigateBack = popBackStack
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun FileDownloaderScaffold(
    navigateBack: () -> Unit
) {
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = rememberScaffoldState(),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.file_downloader_title),
                    onBack = { navigateBack() },
                )
            },
        ) {
            Text(text = "File downloader")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FileDownloaderScaffoldPreview() {
    FileDownloaderScaffold(
        navigateBack = {}
    )
}
