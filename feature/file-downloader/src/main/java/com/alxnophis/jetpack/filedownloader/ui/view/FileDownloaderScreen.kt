package com.alxnophis.jetpack.filedownloader.ui.view

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.extensions.isValidUrl
import com.alxnophis.jetpack.core.ui.composable.CoreButtonMajor
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.filedownloader.R
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderEvent
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderState
import com.alxnophis.jetpack.filedownloader.ui.viewmodel.FileDownloaderViewModel
import com.alxnophis.jetpack.kotlin.constants.EMPTY

@Composable
internal fun FileDownloaderScreen(
    viewModel: FileDownloaderViewModel,
    popBackStack: () -> Unit,
) {
    val state: FileDownloaderState = viewModel.uiState.collectAsState().value
    BackHandler {
        popBackStack()
    }
    FileDownloaderScaffold(
        state = state,
        handleEvent = viewModel::handleEvent,
        navigateBack = popBackStack
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun FileDownloaderScaffold(
    state: FileDownloaderState,
    handleEvent: FileDownloaderEvent.() -> Unit,
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
            FileDownloaderContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(mediumPadding),
                state = state,
                handleEvent = handleEvent
            )
        }
        state.error?.let { error: Int ->
            CoreErrorDialog(
                errorMessage = stringResource(error),
                dismissError = {
                    handleEvent(FileDownloaderEvent.ErrorDismissed)
                }
            )
        }
    }
}

@Composable
private fun FileDownloaderContent(
    modifier: Modifier,
    state: FileDownloaderState,
    handleEvent: FileDownloaderEvent.() -> Unit,
) {
    val downloadFileEvent: () -> Unit = {
        handleEvent.invoke(FileDownloaderEvent.DownloadFile)
    }
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.url,
            singleLine = true,
            onValueChange = { urlValueChanged ->
                handleEvent.invoke(FileDownloaderEvent.UrlChanged(urlValueChanged))
            },
            label = {
                Text(
                    text = stringResource(R.string.file_downloader_url),
                    style = MaterialTheme.typography.body1,
                )
            },
            trailingIcon = {
                IconButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(4.dp),
                    onClick = {
                        handleEvent.invoke(FileDownloaderEvent.UrlChanged(EMPTY))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null
                    )
                }
            },
            keyboardActions = KeyboardActions(
                onSend = { downloadFileEvent() }
            ),
            isError = !state.url.isValidUrl()
        )

        Spacer(modifier = Modifier.height(50.dp))

        CoreButtonMajor(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.file_downloader_download_file),
            isEnabled = state.url.isValidUrl()
        ) {
            downloadFileEvent()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FileDownloaderScaffoldPreview() {
    FileDownloaderScaffold(
        state = FileDownloaderState(url = EMPTY),
        navigateBack = {},
        handleEvent = {},
    )
}
