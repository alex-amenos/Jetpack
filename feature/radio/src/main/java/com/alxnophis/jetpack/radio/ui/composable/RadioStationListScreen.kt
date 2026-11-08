package com.alxnophis.jetpack.radio.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.alxnophis.jetpack.radio.R
import com.alxnophis.jetpack.radio.data.model.RadioStation
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListStatus
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListEvent
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListUiState
import com.alxnophis.jetpack.radio.ui.viewmodel.RadioStationListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioStationListFeature(
    onBack: () -> Unit,
    onStationSelected: () -> Unit,
) {
    val viewModel = koinViewModel<RadioStationListViewModel>()
    RadioStationListScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onBack = onBack,
        onStationSelected = onStationSelected,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RadioStationListScreen(
    uiState: RadioStationListUiState,
    onBack: () -> Unit,
    onStationSelected: () -> Unit,
    handleEvent: (RadioStationListEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.radio_stations_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.radio_back),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        },
    ) { innerPadding ->
        when (uiState.status) {
            RadioStationListStatus.Loading -> {
                LoadingContent(Modifier.padding(innerPadding))
            }

            RadioStationListStatus.Error -> {
                MessageContent(
                    message = stringResource(R.string.radio_stations_error),
                    actionLabel = stringResource(R.string.radio_retry),
                    onAction = { handleEvent(RadioStationListEvent.Retry) },
                    modifier = Modifier.padding(innerPadding),
                )
            }

            RadioStationListStatus.Empty -> {
                MessageContent(
                    message = stringResource(R.string.radio_stations_empty),
                    actionLabel = stringResource(R.string.radio_retry),
                    onAction = { handleEvent(RadioStationListEvent.Retry) },
                    modifier = Modifier.padding(innerPadding),
                )
            }

            RadioStationListStatus.Content -> {
                StationsList(
                    stations = uiState.stations,
                    onStationSelected = { index ->
                        handleEvent(RadioStationListEvent.Select(index))
                        onStationSelected()
                    },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageContent(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = onAction) {
            Text(text = actionLabel)
        }
    }
}

@Composable
private fun StationsList(
    stations: List<RadioStation>,
    onStationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(stations, key = { _, station -> station.id }) { index, station ->
            StationItem(station = station, onClick = { onStationSelected(index) })
        }
    }
}

@Composable
private fun StationItem(
    station: RadioStation,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioStationIcon(faviconUrl = station.faviconUrl, size = 48.dp)
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val details = listOfNotNull(station.codec, station.bitrate?.let { "$it kbps" }).joinToString(" · ")
                if (details.isNotEmpty()) {
                    Text(
                        text = details,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
internal fun RadioStationIcon(
    faviconUrl: String?,
    size: Dp,
) {
    val shape = RoundedCornerShape(size / 4)
    if (faviconUrl == null) {
        RadioIconFallback(size = size, shape = shape)
    } else {
        SubcomposeAsyncImage(
            model = faviconUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .size(size)
                    .clip(shape),
            loading = { RadioIconFallback(size = size, shape = shape) },
            error = { RadioIconFallback(size = size, shape = shape) },
        )
    }
}

@Composable
private fun RadioIconFallback(
    size: Dp,
    shape: Shape,
) {
    Box(
        modifier =
            Modifier
                .size(size)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Radio,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}
