package com.alxnophis.jetpack.filedownloader.ui.composable

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.core.extensions.isValidUrl
import com.alxnophis.jetpack.core.ui.composable.CoreButtonMajor
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.composable.drawVerticalScrollbar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.filedownloader.R
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderUiEvent
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderUiState
import com.alxnophis.jetpack.filedownloader.ui.contract.NO_ERROR
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.THREE_DOTS
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import kotlinx.coroutines.launch

private typealias HandleFileDownloaderUiEvent = FileDownloaderUiEvent.() -> Unit

// Suppress 'ComposeCompositionLocalUsage' because this CompositionLocal is intentionally used
// to provide an event handler to composables in this feature module. This pattern is safe and
// appropriate for dependency injection of event handlers in Compose.
@SuppressLint("ComposeCompositionLocalUsage")
internal val LocalFileDownloaderUiEventHandler =
    staticCompositionLocalOf<HandleFileDownloaderUiEvent> {
        error("No FileDownloaderUiEvent provided")
    }

@Composable
internal fun FileDownloaderScreen(
    uiState: FileDownloaderUiState,
    onEvent: (FileDownloaderUiEvent) -> Unit,
) {
    CompositionLocalProvider(LocalFileDownloaderUiEventHandler provides onEvent) {
        val handleEvent = LocalFileDownloaderUiEventHandler.current
        BackHandler {
            FileDownloaderUiEvent.GoBackRequested.handleEvent()
        }
        LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
            FileDownloaderUiEvent.Initialized.handleEvent()
        }
        FileDownloaderContainer(uiState)
    }
}

@Composable
private fun FileDownloaderContainer(
    uiState: FileDownloaderUiState,
) {
    val handleEvent = LocalFileDownloaderUiEventHandler.current
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.file_downloader_title),
                    onBack = { FileDownloaderUiEvent.GoBackRequested.handleEvent() },
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            FileDownloaderContent(
                uiState = uiState,
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .drawVerticalScrollbar(rememberScrollState())
                        .padding(mediumPadding),
            )

            FileDownloaderErrors(uiState)
        }
    }
}

@Composable
private fun FileDownloaderContent(
    uiState: FileDownloaderUiState,
    modifier: Modifier = Modifier,
) {
    val handleEvent = LocalFileDownloaderUiEventHandler.current
    val downloadFileEvent: () -> Unit = {
        FileDownloaderUiEvent.DownloadFileRequested.handleEvent()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.url,
            singleLine = true,
            onValueChange = { urlValueChanged ->
                FileDownloaderUiEvent.UrlChanged(urlValueChanged).handleEvent()
            },
            label = {
                Text(
                    text = stringResource(R.string.file_downloader_url),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingIcon = {
                IconButton(
                    modifier =
                        Modifier
                            .wrapContentSize()
                            .padding(4.dp),
                    onClick = {
                        FileDownloaderUiEvent.UrlChanged(EMPTY).handleEvent()
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null,
                    )
                }
            },
            keyboardActions =
                KeyboardActions(
                    onSend = { downloadFileEvent() },
                ),
            isError =
                if (uiState.url.isNotEmpty()) {
                    !uiState.url.isValidUrl()
                } else {
                    false
                },
        )

        Spacer(modifier = Modifier.height(25.dp))

        CoreButtonMajor(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.file_downloader_download_file),
            isEnabled = uiState.url.isValidUrl(),
        ) {
            keyboardController?.hide()
            downloadFileEvent()
        }

        Spacer(modifier = Modifier.height(25.dp))

        FileDownloaderDivider()

        Spacer(modifier = Modifier.height(25.dp))

        FileDownloaderList(uiState)
    }
}

@Composable
private fun FileDownloaderDivider() {
    HorizontalDivider(
        modifier =
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .alpha(0.2f)
                .background(MaterialTheme.colorScheme.onBackground),
    )
}

@Composable
private fun FileDownloaderList(
    uiState: FileDownloaderUiState,
) {
    LazyColumn(
        state = rememberLazyListState(),
        userScrollEnabled = true,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(
            items = uiState.fileStatusList,
            key = { item: String -> item.hashCode() },
            itemContent = {
                EllipsizedMiddleText(text = it)
            },
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
    uiState: FileDownloaderUiState,
) {
    val handleEvent = LocalFileDownloaderUiEventHandler.current
    val dismissError: () -> Unit = {
        FileDownloaderUiEvent.ErrorDismissRequested.handleEvent()
    }
    when {
        uiState.error == R.string.file_downloader_generic_error ->
            DialogError(
                error = uiState.error,
                onDismiss = dismissError,
            )

        uiState.error != NO_ERROR ->
            SnackbarError(
                modifier = Modifier.fillMaxSize(),
                error = uiState.error,
                onDismiss = dismissError,
            )

        else -> doNothing()
    }
}

@Composable
private fun DialogError(
    error: Int,
    onDismiss: () -> Unit,
) {
    CoreErrorDialog(
        errorMessage = stringResource(error),
        dismissError = onDismiss,
    )
}

@Composable
private fun SnackbarError(
    error: Int,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    val errorMessage = stringResource(id = error)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(error) {
        scope.launch {
            val result =
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    actionLabel = null,
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
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { snackbarData: SnackbarData ->
            Snackbar(
                modifier = Modifier.padding(10.dp),
                action = {},
            ) {
                Text(snackbarData.toString())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FileDownloaderScaffoldPreview() {
    val uiState =
        FileDownloaderUiState(
            url = EMPTY,
            error = R.string.core_error_title,
            fileStatusList =
                listOf(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                ),
        )
    FileDownloaderScreen(
        uiState = uiState,
        onEvent = {},
    )
}
