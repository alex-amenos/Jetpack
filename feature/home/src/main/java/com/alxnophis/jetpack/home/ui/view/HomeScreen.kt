package com.alxnophis.jetpack.home.ui.view

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.extraSmallPadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.home.R
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import com.alxnophis.jetpack.router.screen.Screen

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel,
    backOrFinish: (Activity?) -> Unit,
    navigateTo: (String) -> Unit
) {
    val state: HomeState = viewModel.uiState.collectAsStateWithLifecycle().value
    val activity: Activity? = (LocalContext.current as? Activity)
    BackHandler {
        backOrFinish(activity)
    }
    HomeContent(
        state = state,
        handleEvent = viewModel::handleEvent,
        navigateTo = { route -> navigateTo(route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeContent(
    state: HomeState,
    handleEvent: HomeEvent.() -> Unit,
    navigateTo: (route: String) -> Unit
) {
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { HomeTopBar() }
        ) {
            Column(
                modifier = Modifier.padding(paddingValues = it)
            ) {
                SectionsList(
                    state = state,
                    navigateTo = navigateTo,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
                )
            }
            state.error?.let { error: Int ->
                CoreErrorDialog(
                    errorMessage = stringResource(error),
                    dismissError = { handleEvent.invoke(HomeEvent.ErrorDismissed) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text(
                text = stringResource(id = R.string.home_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    )
}

@Composable
internal fun SectionsList(
    state: HomeState,
    modifier: Modifier = Modifier,
    navigateTo: (route: String) -> Unit
) {
    LazyColumn(
        state = rememberLazyListState(),
        modifier = modifier
    ) {
        items(
            items = state.data,
            key = { item: NavigationItem -> item.id },
            itemContent = { item: NavigationItem ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { navigateTo(item.screen.route) }
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(mediumPadding)
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = item.emoji,
                        fontWeight = FontWeight.Medium
                    )
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxWidth()
                            .padding(start = mediumPadding)
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = item.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(top = extraSmallPadding),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = item.description,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
                Divider(color = Color.LightGray)
            }
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val state = HomeState(
        data = listOf(
            NavigationItem(
                name = "Screen 1",
                emoji = "🐻",
                description = "Lorem ipsum",
                screen = Screen.Authentication
            ),
            NavigationItem(
                name = "Screen 2",
                emoji = "🦊",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                screen = Screen.Settings
            )
        ),
        error = null
    )
    HomeContent(
        state = state,
        handleEvent = {},
        navigateTo = {}
    )
}
