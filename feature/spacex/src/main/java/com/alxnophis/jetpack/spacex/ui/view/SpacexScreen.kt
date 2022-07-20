package com.alxnophis.jetpack.spacex.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.alxnophis.jetpack.spacex.R
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesEvent
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchesModel
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
        onRefresh = { onLaunchesEvent.invoke(LaunchesEvent.GetPastLaunches) }
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
                itemContent = { item: PastLaunchesModel ->
                    PastLaunchItem(item)
                }
            )
        }
    }
}

@Composable
private fun PastLaunchItem(item: PastLaunchesModel) {
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
            if (item.launch_date_utc.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.End)
                        .wrapContentWidth(),
                    text = item.launch_date_utc,
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                )
            }
            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp),
                    model = ImageRequest
                        .Builder(localContext)
                        .data(item.mission_patch_url)
                        .fallback(R.drawable.ic_rocket_launch)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_rocket_launch),
                    contentDescription = item.mission_name,
                    contentScale = ContentScale.FillBounds,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = item.mission_name,
                        color = MaterialTheme.colors.primary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .wrapContentSize(),
                        text = item.rocket,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            if (item.details.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .wrapContentSize(),
                    text = item.details,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpacexContentPreview() {
    val state = LaunchesState(
        isLoading = false,
        pastLaunches = listOf(
            PastLaunchesModel(
                id = "1",
                mission_name = "Mission XYZ",
                details = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                rocket = "Rocket Name (Company)",
                launchSite = "Launch Site",
                mission_patch_url = null,
                launch_date_utc = "10 May 21 - 11:00"
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
