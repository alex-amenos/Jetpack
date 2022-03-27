package com.alxnophis.jetpack.home.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.home.R
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.contract.HomeViewAction
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.getViewModel

@Composable
internal fun HomeComposable(
    viewModel: HomeViewModel = getViewModel()
) {
    CoreTheme {
        val state = viewModel.uiState.collectAsState().value
        HomeScreen(
            state = state,
            onViewAction = viewModel::setAction
        )
    }
}

@Composable
internal fun HomeScreen(
    state: HomeState,
    onViewAction: (viewAction: HomeViewAction) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeTopBar()
            NavigationScreens(state, onViewAction)
        }
        state.error?.let { error: Int ->
            CoreErrorDialog(
                errorMessage = stringResource(error),
                dismissError = { onViewAction.invoke(HomeViewAction.ErrorDismissed) }
            )
        }
    }
}

@Composable
internal fun HomeTopBar() {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentPadding = PaddingValues(start = 12.dp)
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.home_title),
            color = MaterialTheme.colors.onPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
internal fun NavigationScreens(
    state: HomeState,
    onViewAction: (viewAction: HomeViewAction) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        items(
            items = state.data,
            itemContent = { item: NavigationItem ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            onViewAction.invoke(HomeViewAction.NavigateTo(item.intent))
                        }
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            style = MaterialTheme.typography.subtitle1,
                            text = item.name,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            style = MaterialTheme.typography.subtitle1,
                            text = item.description,
                            fontWeight = FontWeight.Light,
                        )
                    }
                    Icon(
                        modifier = Modifier.weight(0.1f),
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                    )
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
                description = stringResource(id = R.string.core_lorem_ipsum_s),
                intent = null
            ),
            NavigationItem(
                name = "Screen 2",
                description = stringResource(id = R.string.core_lorem_ipsum_m),
                intent = null
            ),
        ),
        error = null
    )
    CoreTheme {
        HomeScreen(
            state = state,
            onViewAction = {}
        )
    }
}
