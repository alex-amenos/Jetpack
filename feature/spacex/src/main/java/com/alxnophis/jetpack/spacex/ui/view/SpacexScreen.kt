package com.alxnophis.jetpack.spacex.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alxnophis.jetpack.core.ui.composable.ComposableLifecycle
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.composable.drawVerticalScrollbar
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.extraSmallPadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.spacex.R
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesEvent
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel
import com.alxnophis.jetpack.spacex.ui.view.SpacexTags.TAG_SPACEX_LAUNCH_DETAIL
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
internal fun SpacexScreen(
    state: LaunchesState,
    onEvent: (LaunchesEvent) -> Unit = {}
) {
    BackHandler { onEvent(LaunchesEvent.GoBackRequested) }
    ComposableLifecycle { _, event ->
        if (event == Lifecycle.Event.ON_CREATE) {
            onEvent(LaunchesEvent.Initialized)
        }
    }
    AppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.spacex_title),
                    onBack = { onEvent(LaunchesEvent.GoBackRequested) }
                )
            }
        ) { paddingValues ->
            PastLaunchesList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                state = state,
                onEvent = onEvent
            )
            state.errorMessages.firstOrNull()?.let { error: ErrorMessage ->
                CoreErrorDialog(
                    errorMessage = error.composableMessage(),
                    dismissError = { onEvent(LaunchesEvent.DismissErrorRequested(error.id)) }
                )
            }
        }
    }
}

@Composable
private fun PastLaunchesList(
    state: LaunchesState,
    modifier: Modifier = Modifier,
    onEvent: LaunchesEvent.() -> Unit
) {
    val listState = rememberLazyListState()
    SwipeRefresh(
        modifier = modifier,
        state = rememberSwipeRefreshState(state.isLoading),
        onRefresh = { onEvent.invoke(LaunchesEvent.RefreshPastLaunchesRequested) }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .drawVerticalScrollbar(listState),
            contentPadding = PaddingValues(mediumPadding)
        ) {
            items(
                items = state.pastLaunches,
                key = { item: PastLaunchModel -> item.id + item.missionName },
                itemContent = { item: PastLaunchModel ->
                    PastLaunchItem(
                        item = item,
                        modifier = Modifier
                            .padding(vertical = mediumPadding)
                            .fillMaxWidth()
                    )
                }
            )
        }
    }
}

@Composable
private fun PastLaunchItem(
    item: PastLaunchModel,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(mediumPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (item.launchSite.isNotEmpty()) {
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = item.launchSite,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                if (item.launchDateUtc.isNotEmpty()) {
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = item.launchDateUtc,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = mediumPadding)
            ) {
                MissionImage(
                    modifier = Modifier.size(60.dp),
                    missionPatchUrl = item.missionPatchUrl,
                    imageContentDescription = item.missionName
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = mediumPadding)
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = item.missionName,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = extraSmallPadding)
                            .wrapContentSize(),
                        text = item.rocket,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            if (item.details.isNotEmpty()) {
                Text(
                    text = item.details,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(top = mediumPadding)
                        .wrapContentSize()
                        .testTag(TAG_SPACEX_LAUNCH_DETAIL + item.id)
                )
            }
        }
    }
}

@Composable
private fun MissionImage(
    missionPatchUrl: String?,
    imageContentDescription: String?,
    modifier: Modifier = Modifier
) {
    if (missionPatchUrl == null) {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.spacex_ic_rocket_launch),
            contentDescription = imageContentDescription
        )
    } else {
        val localContext = LocalContext.current
        AsyncImage(
            modifier = modifier,
            model = ImageRequest
                .Builder(localContext)
                .data(missionPatchUrl)
                .fallback(R.drawable.spacex_ic_rocket_launch)
                .diskCacheKey(missionPatchUrl)
                .crossfade(true)
                .build(),
            contentDescription = imageContentDescription,
            contentScale = ContentScale.FillBounds
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SpacexContentPreview() {
    val pastLaunch = PastLaunchModel(
        id = "1",
        missionName = "Mission XYZ",
        details = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.".repeat(5),
        rocket = "Rocket Name (Company)",
        launchSite = "Launch Site",
        missionPatchUrl = null,
        launchDateUtc = "10 May 21 - 11:00"
    )
    val state = LaunchesState(
        isLoading = false,
        pastLaunches = listOf(pastLaunch),
        errorMessages = emptyList()
    )
    SpacexScreen(state)
}
