package com.alxnophis.jetpack.myplayground.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.myplayground.R
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundEvent
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundState
import com.alxnophis.jetpack.myplayground.ui.viewmodel.MyPlaygroundViewModel

@Composable
internal fun MyPlaygroundScreen(
    popBackStack: () -> Unit,
    viewModel: MyPlaygroundViewModel
) {
    val state: MyPlaygroundState = viewModel.uiState.collectAsStateWithLifecycle().value
    BackHandler {
        popBackStack()
    }
    MyPlaygroundScaffold(
        state = state,
        navigateBack = popBackStack,
        handleEvent = { event: MyPlaygroundEvent -> viewModel.handleEvent(event) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyPlaygroundScaffold(
    state: MyPlaygroundState,
    navigateBack: () -> Unit,
    handleEvent: (MyPlaygroundEvent) -> Unit
) {
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.myplayground_title),
                    onBack = { navigateBack() }
                )
            }
        ) { paddingValues ->
            MyPlaygroundContent(
                state = state,
                handleEvent = handleEvent,
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize()
                    .padding(mediumPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyPlaygroundContent(
    state: MyPlaygroundState,
    modifier: Modifier = Modifier,
    handleEvent: (MyPlaygroundEvent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.focusRequester(focusRequester),
            textStyle = TextStyle(fontFamily = FontFamily.Monospace),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
            placeholder = {
                Text(text = stringResource(R.string.myplayground_quote))
            },
            value = state.textFieldValue,
            onValueChange = { handleEvent(MyPlaygroundEvent.TextFieldChanged(it)) }
        )
    }
}

@Preview
@Composable
private fun MyPlaygroundScaffoldPreview() {
    val state = MyPlaygroundState.initialState
    MyPlaygroundScaffold(
        state = state,
        handleEvent = {},
        navigateBack = {}
    )
}
