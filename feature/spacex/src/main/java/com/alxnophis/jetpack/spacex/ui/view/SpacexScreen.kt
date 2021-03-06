package com.alxnophis.jetpack.spacex.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.composable.drawVerticalScrollbar
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.alxnophis.jetpack.spacex.R
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesEvent
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel
import com.alxnophis.jetpack.spacex.ui.view.SpacexTags.TAG_SPACEX_LAUNCH_DETAIL
import com.alxnophis.jetpack.spacex.ui.viewmodel.LaunchesViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
internal fun SpacexScreen(
    navController: NavController,
    viewModel: LaunchesViewModel
) {
    val navigateBack: () -> Unit = { navController.popBackStack() }
    BackHandler { navigateBack() }
    SpacexContent(
        state = viewModel.uiState.collectAsState().value,
        onLaunchesEvent = viewModel::handleEvent,
        onNavigateBack = navigateBack
    )
}

@Composable
internal fun SpacexContent(
    state: LaunchesState,
    onLaunchesEvent: (event: LaunchesEvent) -> Unit,
    onNavigateBack: () -> Unit,
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
            PastLaunchesList(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onLaunchesEvent = onLaunchesEvent
            )
            state.errorMessages.firstOrNull()?.let { error: ErrorMessage ->
                CoreErrorDialog(
                    errorMessage = stringResource(error.messageId),
                    dismissError = { onLaunchesEvent.invoke(LaunchesEvent.DismissError(error.id)) }
                )
            }
        }
    }
}

@Composable
private fun PastLaunchesList(
    modifier: Modifier,
    state: LaunchesState,
    onLaunchesEvent: (event: LaunchesEvent) -> Unit
) {
    val listState = rememberLazyListState()
    SwipeRefresh(
        state = rememberSwipeRefreshState(state.isLoading),
        onRefresh = { onLaunchesEvent.invoke(LaunchesEvent.RefreshPastLaunches) }
    ) {
        LazyColumn(
            state = listState,
            modifier = modifier
                .background(MaterialTheme.colors.surface)
                .drawVerticalScrollbar(listState),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                items = state.pastLaunches,
                itemContent = { item: PastLaunchModel ->
                    PastLaunchItem(item)
                }
            )
        }
    }
}

@Composable
private fun PastLaunchItem(item: PastLaunchModel) {
    val localContext = LocalContext.current
    Card(
        elevation = 10.dp,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (item.launchSite.isNotEmpty()) {
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = item.launchSite,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light,
                    )
                }
                if (item.launchDateUtc.isNotEmpty()) {
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = item.launchDateUtc,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light,
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                AsyncImage(
                    modifier = Modifier.size(60.dp),
                    model = ImageRequest
                        .Builder(localContext)
                        .data(item.missionPatchUrl)
                        .fallback(R.drawable.ic_rocket_launch)
                        .diskCacheKey(item.missionPatchUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.missionName,
                    contentScale = ContentScale.FillBounds
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = item.missionName,
                        color = MaterialTheme.colors.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .wrapContentSize(),
                        text = item.rocket,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            if (item.details.isNotEmpty()) {
                ExpandingText(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .wrapContentSize()
                        .testTag(TAG_SPACEX_LAUNCH_DETAIL + item.id),
                    item = item,
                )
            }
        }
    }
}

@Composable
private fun ExpandingText(
    modifier: Modifier = Modifier,
    item: PastLaunchModel,
) {
    val showMoreString = stringResource(R.string.spacex_show_more)
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val textLayoutResult = textLayoutResultState.value
    var isClickable by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    var finalText: String by remember { mutableStateOf(item.details) }
    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect
        when {
            isExpanded -> {
                isClickable = false
                finalText = item.details
            }
            !isExpanded && textLayoutResult.hasVisualOverflow -> {
                isClickable = true
                val lastCharIndex = textLayoutResult.getLineEnd(MINIMIZED_LINES - 1)
                val adjustedText = item.details
                    .substring(startIndex = ZERO_INT, endIndex = lastCharIndex)
                    .dropLast(showMoreString.length)
                    .dropLastWhile { it == ' ' || it == '.' }
                finalText = "$adjustedText$showMoreString"
            }
        }
    }
    Text(
        text = finalText,
        modifier = modifier
            .clickable(enabled = isClickable) { isExpanded = !isExpanded }
            .animateContentSize(),
        fontSize = 14.sp,
        fontWeight = FontWeight.Light,
        maxLines = if (isExpanded) Int.MAX_VALUE else MINIMIZED_LINES,
        onTextLayout = { textLayoutResultState.value = it },
    )
}

@Preview(showBackground = true)
@Composable
private fun SpacexContentPreview() {
    val state = LaunchesState(
        isLoading = false,
        pastLaunches = listOf(
            PastLaunchModel(
                id = "1",
                missionName = "Mission XYZ",
                details = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                rocket = "Rocket Name (Company)",
                launchSite = "Launch Site",
                missionPatchUrl = null,
                launchDateUtc = "10 May 21 - 11:00"
            )
        ),
        errorMessages = listOf()
    )
    SpacexContent(
        state = state,
        onLaunchesEvent = {},
        onNavigateBack = {}
    )
}

private const val MINIMIZED_LINES = 3
