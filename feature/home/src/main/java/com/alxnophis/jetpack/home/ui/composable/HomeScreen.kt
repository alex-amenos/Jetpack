package com.alxnophis.jetpack.home.ui.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.extraSmallPadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.home.R
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.ui.composable.provider.HomePreviewProvider
import com.alxnophis.jetpack.home.ui.contract.HomeUiState

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onNavigateTo: (Feature) -> Unit = {},
    onBack: () -> Unit = {},
) {
    BackHandler {
        onBack()
    }
    AppTheme {
        Scaffold(
            topBar = { HomeTopBar() },
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            SectionsList(
                uiState = uiState,
                modifier =
                    Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(paddingValues),
                navigateTo = onNavigateTo,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTopBar() {
    TopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        title = {
            Text(
                text = stringResource(id = R.string.home_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
        },
    )
}

@Composable
internal fun SectionsList(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    navigateTo: (feature: Feature) -> Unit,
) {
    LazyColumn(
        state = rememberLazyListState(),
        modifier = modifier,
    ) {
        items(
            items = uiState.navigationItems,
            key = { item: NavigationItem -> item.id },
            itemContent = { item: NavigationItem ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .clickable { navigateTo(item.feature) }
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(mediumPadding),
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = item.emoji,
                        fontWeight = FontWeight.Medium,
                    )
                    Column(
                        modifier =
                            Modifier
                                .weight(0.9f)
                                .fillMaxWidth()
                                .padding(start = mediumPadding),
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = item.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            modifier =
                                Modifier
                                    .wrapContentSize()
                                    .padding(top = extraSmallPadding),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = item.description,
                            fontWeight = FontWeight.Light,
                        )
                    }
                }
                HorizontalDivider(color = Color.LightGray)
            },
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(HomePreviewProvider::class) uiState: HomeUiState,
) {
    HomeScreen(uiState)
}
