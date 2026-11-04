package com.alxnophis.jetpack.radio.ui.composable

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.radio.R
import com.alxnophis.jetpack.radio.playback.PlaybackStatus
import com.alxnophis.jetpack.radio.ui.contract.PlayerUiState
import com.alxnophis.jetpack.radio.ui.contract.RadioPlayerEvent
import com.alxnophis.jetpack.radio.ui.viewmodel.RadioPlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioPlayerFeature(onBack: () -> Unit) {
    NotificationPermissionRequest()
    val viewModel = koinViewModel<RadioPlayerViewModel>()
    RadioPlayerScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onBack = onBack,
        handleEvent = viewModel::handleEvent,
    )
}

@Composable
private fun NotificationPermissionRequest() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        val needsPermission =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RadioPlayerScreen(
    uiState: PlayerUiState,
    onBack: () -> Unit,
    handleEvent: (RadioPlayerEvent) -> Unit,
) {
    var showTimerSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterVertically),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StationIcon(faviconUrl = uiState.artworkUri, size = 200.dp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = uiState.stationName.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(14.dp))
                LiveChip()
                if (uiState.status == PlaybackStatus.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorMessage(onRetry = { handleEvent(RadioPlayerEvent.Retry) })
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ControlsRow(
                    uiState = uiState,
                    onAction = handleEvent,
                )
                Spacer(modifier = Modifier.height(24.dp))
                SecondaryRow(
                    uiState = uiState,
                    onStop = { handleEvent(RadioPlayerEvent.Stop) },
                    onTimer = { showTimerSheet = true },
                )
                uiState.sleepTimerRemainingMillis?.let { remaining ->
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.radio_sleep_remaining, formatRemaining(remaining)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        BackButton(
            onClick = onBack,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = 8.dp),
        )
    }

    if (showTimerSheet) {
        SleepTimerSheet(
            onDismiss = { showTimerSheet = false },
            onSelect = { minutes ->
                handleEvent(RadioPlayerEvent.SetSleepTimer(minutes))
                showTimerSheet = false
            },
        )
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.radio_back),
        )
    }
}

@Composable
private fun LiveChip() {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = "● " + stringResource(R.string.radio_live),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun ErrorMessage(onRetry: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(imageVector = Icons.Filled.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
            Text(
                text = stringResource(R.string.radio_offline_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.radio_retry))
            }
        }
    }
}

@Composable
private fun ControlsRow(
    uiState: PlayerUiState,
    onAction: (RadioPlayerEvent) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        RoundControl(
            icon = Icons.Filled.SkipPrevious,
            description = stringResource(R.string.radio_previous),
            size = 68.dp,
            enabled = uiState.canPrevious,
            onClick = { onAction(RadioPlayerEvent.Previous) },
        )
        PlayPauseButton(uiState = uiState, onAction = onAction)
        RoundControl(
            icon = Icons.Filled.SkipNext,
            description = stringResource(R.string.radio_next),
            size = 68.dp,
            enabled = uiState.canNext,
            onClick = { onAction(RadioPlayerEvent.Next) },
        )
    }
}

@Composable
private fun PlayPauseButton(
    uiState: PlayerUiState,
    onAction: (RadioPlayerEvent) -> Unit,
) {
    val isPlaying = uiState.status == PlaybackStatus.Playing
    val isError = uiState.status == PlaybackStatus.Error
    val onClick: () -> Unit =
        when {
            isError -> {
                { onAction(RadioPlayerEvent.Retry) }
            }

            isPlaying -> {
                { onAction(RadioPlayerEvent.Pause) }
            }

            else -> {
                { onAction(RadioPlayerEvent.Play) }
            }
        }
    Box(
        modifier =
            Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            uiState.status == PlaybackStatus.Buffering -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(44.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            isError -> {
                Icon(
                    Icons.Filled.PlayArrow,
                    stringResource(R.string.radio_retry),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp),
                )
            }

            isPlaying -> {
                Icon(
                    Icons.Filled.Pause,
                    stringResource(R.string.radio_pause),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp),
                )
            }

            else -> {
                Icon(
                    Icons.Filled.PlayArrow,
                    stringResource(R.string.radio_play),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp),
                )
            }
        }
    }
}

@Composable
private fun SecondaryRow(
    uiState: PlayerUiState,
    onStop: () -> Unit,
    onTimer: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        RoundControl(
            icon = Icons.Filled.Stop,
            description = stringResource(R.string.radio_stop),
            size = 60.dp,
            enabled = uiState.status != PlaybackStatus.Idle,
            onClick = onStop,
        )
        RoundControl(
            icon = Icons.Filled.Timer,
            description = stringResource(R.string.radio_sleep_timer),
            size = 60.dp,
            enabled = true,
            onClick = onTimer,
        )
    }
}

@Composable
private fun RoundControl(
    icon: ImageVector,
    description: String,
    size: Dp,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier =
            Modifier
                .size(size)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
    ) {
        Icon(imageVector = icon, contentDescription = description, modifier = Modifier.size(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SleepTimerSheet(
    onDismiss: () -> Unit,
    onSelect: (Int?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = stringResource(R.string.radio_sleep_title), style = MaterialTheme.typography.titleLarge)
            listOf(15, 30, 60).forEach { minutes ->
                OutlinedButton(onClick = { onSelect(minutes) }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.radio_sleep_minutes, minutes))
                }
            }
            Button(onClick = { onSelect(null) }, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.radio_sleep_off))
            }
        }
    }
}

private fun formatRemaining(millis: Long): String {
    val totalSeconds = millis / 1_000
    return "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
