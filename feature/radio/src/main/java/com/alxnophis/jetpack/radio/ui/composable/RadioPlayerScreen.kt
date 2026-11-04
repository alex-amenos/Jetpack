package com.alxnophis.jetpack.radio.ui.composable

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.core.ui.theme.AppTheme
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
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Composable
internal fun RadioPlayerScreen(
    uiState: PlayerUiState,
    onBack: () -> Unit,
    handleEvent: (RadioPlayerEvent) -> Unit,
) {
    var showTimerSheet by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        val isLandscape = maxWidth > maxHeight
        if (isLandscape) {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                PlayerInfo(
                    uiState = uiState,
                    handleEvent = handleEvent,
                    iconSize = 160.dp,
                    modifier = Modifier.weight(1f),
                )
                PlayerControls(
                    uiState = uiState,
                    handleEvent = handleEvent,
                    onTimer = { showTimerSheet = true },
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterVertically),
            ) {
                PlayerInfo(
                    uiState = uiState,
                    handleEvent = handleEvent,
                    iconSize = 200.dp,
                )
                PlayerControls(
                    uiState = uiState,
                    handleEvent = handleEvent,
                    onTimer = { showTimerSheet = true },
                )
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

        if (uiState.status == PlaybackStatus.Error) {
            ErrorBanner(
                onRetry = { handleEvent(RadioPlayerEvent.Retry) },
                modifier =
                    if (isLandscape) {
                        Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(top = 16.dp)
                            .widthIn(max = BANNER_MAX_WIDTH)
                    } else {
                        Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(start = 80.dp, end = 24.dp, top = 16.dp)
                    },
            )
        }
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

private const val ICON_RATIO = 0.6f
private val PRIMARY_ICON_SIZE = 64.dp
private val RIPPLE_CORNER_RADIUS = 16.dp
private val BANNER_MAX_WIDTH = 420.dp

@Composable
private fun PlayerInfo(
    uiState: PlayerUiState,
    handleEvent: (RadioPlayerEvent) -> Unit,
    iconSize: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StationIcon(faviconUrl = uiState.artworkUri, size = iconSize)
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
    }
}

@Composable
private fun PlayerControls(
    uiState: PlayerUiState,
    handleEvent: (RadioPlayerEvent) -> Unit,
    onTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ControlsRow(
            uiState = uiState,
            onAction = handleEvent,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SecondaryRow(
            uiState = uiState,
            onStop = { handleEvent(RadioPlayerEvent.Stop) },
            onTimer = onTimer,
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

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        shape = CircleShape,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
private fun ErrorBanner(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = stringResource(R.string.radio_offline_message),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            TextButton(onClick = onRetry) {
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
            icon = Icons.Rounded.SkipPrevious,
            description = stringResource(R.string.radio_previous),
            size = 68.dp,
            enabled = uiState.canPrevious,
            onClick = { onAction(RadioPlayerEvent.Previous) },
        )
        PlayPauseButton(uiState = uiState, onAction = onAction)
        RoundControl(
            icon = Icons.Rounded.SkipNext,
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
                .clip(RoundedCornerShape(RIPPLE_CORNER_RADIUS))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            uiState.status == PlaybackStatus.Buffering -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(44.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            isError -> {
                Icon(
                    Icons.Rounded.PlayArrow,
                    stringResource(R.string.radio_retry),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(PRIMARY_ICON_SIZE),
                )
            }

            isPlaying -> {
                Icon(
                    Icons.Rounded.Pause,
                    stringResource(R.string.radio_pause),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(PRIMARY_ICON_SIZE),
                )
            }

            else -> {
                Icon(
                    Icons.Rounded.PlayArrow,
                    stringResource(R.string.radio_play),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(PRIMARY_ICON_SIZE),
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
            icon = Icons.Rounded.Stop,
            description = stringResource(R.string.radio_stop),
            size = 60.dp,
            enabled = uiState.status != PlaybackStatus.Idle,
            onClick = onStop,
        )
        RoundControl(
            icon = Icons.Rounded.Timer,
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
    Box(
        modifier =
            Modifier
                .size(size)
                .clip(RoundedCornerShape(RIPPLE_CORNER_RADIUS))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.38f),
            modifier = Modifier.size(size * ICON_RATIO),
        )
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

@PreviewLightDark
@Preview(name = "Landscape", widthDp = 891, heightDp = 411, showBackground = true)
@Composable
private fun RadioPlayerScreenPreview() {
    AppTheme {
        RadioPlayerScreen(
            uiState =
                PlayerUiState(
                    stationName = "Catalunya Ràdio",
                    artworkUri = null,
                    status = PlaybackStatus.Playing,
                    canPrevious = true,
                    canNext = true,
                    sleepTimerRemainingMillis = 754_000L,
                ),
            onBack = {},
            handleEvent = {},
        )
    }
}

@PreviewLightDark
@Preview(name = "Landscape", widthDp = 891, heightDp = 411, showBackground = true)
@Composable
private fun RadioPlayerScreenErrorPreview() {
    AppTheme {
        RadioPlayerScreen(
            uiState =
                PlayerUiState(
                    stationName = "Cadena SER",
                    artworkUri = null,
                    status = PlaybackStatus.Error,
                    canPrevious = false,
                    canNext = false,
                    sleepTimerRemainingMillis = null,
                ),
            onBack = {},
            handleEvent = {},
        )
    }
}
