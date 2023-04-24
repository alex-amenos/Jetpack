package com.alxnophis.jetpack.filedownloader.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.core.extensions.isValidUrl
import com.alxnophis.jetpack.core.ui.composable.CoreButtonMajor
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.composable.drawVerticalScrollbar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.filedownloader.R
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderEvent
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderState
import com.alxnophis.jetpack.filedownloader.ui.viewmodel.FileDownloaderViewModel
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.THREE_DOTS
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import kotlinx.coroutines.launch

@Composable
internal fun FileDownloaderScreen(
    viewModel: FileDownloaderViewModel,
    popBackStack: () -> Unit
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileDownloaderScaffold(
    state: FileDownloaderState,
    handleEvent: FileDownloaderEvent.() -> Unit,
    navigateBack: () -> Unit
) {
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.file_downloader_title),
                    onBack = { navigateBack() }
                )
            }
        ) { paddingValues ->
            FileDownloaderContent(
                state = state,
                handleEvent = handleEvent,
                modifier = Modifier
                    .padding(paddingValues)
                    .drawVerticalScrollbar(rememberScrollState())
                    .fillMaxSize()
                    .padding(mediumPadding)
            )
            FileDownloaderErrors(
                state = state,
                dismissError = { handleEvent(FileDownloaderEvent.ErrorDismissed) }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun FileDownloaderContent(
    state: FileDownloaderState,
    modifier: Modifier = Modifier,
    handleEvent: FileDownloaderEvent.() -> Unit
) {
    val downloadFileEvent: () -> Unit = {
        handleEvent.invoke(FileDownloaderEvent.DownloadFile)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
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
                    style = MaterialTheme.typography.bodyMedium
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
            isError = if (state.url.isNotEmpty()) {
                !state.url.isValidUrl()
            } else {
                false
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        CoreButtonMajor(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.file_downloader_download_file),
            isEnabled = state.url.isValidUrl()
        ) {
            keyboardController?.hide()
            downloadFileEvent()
        }

        Spacer(modifier = Modifier.height(25.dp))

        FileDownloaderDivider()

        Spacer(modifier = Modifier.height(25.dp))

        FileDownloaderList(state.fileStatusList)
    }
}

@Composable
private fun FileDownloaderDivider() {
    Divider(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .alpha(0.2f)
            .background(MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
private fun FileDownloaderList(list: List<String>) {
    LazyColumn(
        state = rememberLazyListState(),
        userScrollEnabled = true,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = list,
            key = { item: String -> item.hashCode() },
            itemContent = {
                EllipsizedMiddleText(text = it)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Suppress("SameParameterValue")
@Composable
private fun EllipsizedMiddleText(text: String) {
    val firstHalfLength = 15
    val lastHalfLength = 20
    val modifier = Modifier.padding(vertical = 8.dp)
    val style = MaterialTheme.typography.bodyMedium
    if (text.length <= firstHalfLength + lastHalfLength) {
        Text(modifier = modifier, style = style, text = text)
    } else {
        val firstHalf = text.substring(ZERO_INT, firstHalfLength)
        val secondHalf = text.substring(text.length - lastHalfLength)
        FlowRow {
            Text(modifier = modifier, style = style, text = firstHalf)
            Text(modifier = modifier, style = style, text = THREE_DOTS)
            Text(modifier = modifier, style = style, text = secondHalf)
        }
    }
}

@Composable
private fun FileDownloaderErrors(
    state: FileDownloaderState,
    dismissError: () -> Unit
) {
    when {
        state.error == R.string.file_downloader_generic_error -> DialogError(
            error = state.error,
            onDismiss = dismissError
        )

        state.error != null -> SnackbarError(
            modifier = Modifier.fillMaxSize(),
            error = state.error,
            onDismiss = dismissError
        )

        else -> doNothing()
    }
}

@Composable
private fun DialogError(
    error: Int,
    onDismiss: () -> Unit
) {
    CoreErrorDialog(
        errorMessage = stringResource(error),
        dismissError = onDismiss
    )
}

@Composable
private fun SnackbarError(
    error: Int,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val errorMessage = stringResource(id = error)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(error) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = null
            )
            when (result) {
                SnackbarResult.Dismissed -> onDismiss()
                SnackbarResult.ActionPerformed -> doNothing()
            }
        }
    }
    Box(modifier) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { snackbarData: SnackbarData ->
            Snackbar(
                modifier = Modifier.padding(10.dp),
                action = {}
            ) {
                Text(snackbarData.toString())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FileDownloaderScaffoldPreview() {
    val state = FileDownloaderState(
        url = EMPTY,
        error = com.alxnophis.jetpack.core.R.string.core_error_title,
        fileStatusList = listOf(
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
        )
    )
    FileDownloaderScaffold(
        state = state,
        navigateBack = {},
        handleEvent = {}
    )
}
