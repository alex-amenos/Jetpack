package com.alxnophis.jetpack.game.ballclicker.ui.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.DISABLED_CONTENT
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.core.ui.theme.smallPadding
import com.alxnophis.jetpack.game.ballclicker.R
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerState
import com.alxnophis.jetpack.kotlin.constants.WHITE_SPACE
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
internal fun BallClickerScreen(
    state: BallClickerState,
    onEvent: (BallClickerEvent) -> Unit,
) {
    BackHandler {
        onEvent(BallClickerEvent.StopRequested)
        onEvent(BallClickerEvent.GoBackRequested)
    }
    AppTheme {
        Scaffold(
            topBar = {
                Row(
                    modifier =
                        Modifier
                            .background(color = MaterialTheme.colorScheme.primary)
                            .statusBarsPadding()
                            .displayCutoutPadding()
                            .fillMaxWidth()
                            .padding(mediumPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text =
                            buildString {
                                append(stringResource(R.string.ball_clicker_points))
                                append(WHITE_SPACE)
                                append(state.points)
                            },
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = state.currentTimeInSeconds.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Button(
                        onClick = {
                            if (state.isTimerRunning) {
                                onEvent(BallClickerEvent.StopRequested)
                            } else {
                                onEvent(BallClickerEvent.StartRequested)
                            }
                        },
                        colors =
                            ButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = DISABLED_CONTENT),
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = DISABLED_CONTENT),
                            ),
                        border =
                            BorderStroke(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            ),
                        modifier =
                            Modifier
                                .wrapContentWidth()
                                .padding(horizontal = smallPadding),
                    ) {
                        Text(
                            text =
                                when {
                                    state.isTimerRunning -> stringResource(R.string.ball_clicker_reset)
                                    else -> stringResource(R.string.ball_clicker_start)
                                },
                        )
                    }
                }
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.primary),
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            BallClickerContent(
                enabled = state.isTimerRunning,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                onEvent(BallClickerEvent.BallClicked)
            }
        }
    }
}

@Composable
private fun BallClickerContent(
    modifier: Modifier = Modifier,
    radius: Float = 100f,
    enabled: Boolean = false,
    ballColor: Color = MaterialTheme.colorScheme.secondary,
    ballClick: () -> Unit = {},
) {
    BoxWithConstraints(modifier = modifier) {
        var ballPosition by remember {
            mutableStateOf(
                randomOffset(
                    radius = radius,
                    width = constraints.maxWidth,
                    height = constraints.maxHeight,
                ),
            )
        }
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .pointerInput(enabled) {
                        if (!enabled) {
                            return@pointerInput
                        }
                        detectTapGestures {
                            val distance = sqrt((it.x - ballPosition.x).pow(2) + (it.y - ballPosition.y).pow(2))
                            if (distance <= radius) {
                                ballPosition =
                                    randomOffset(
                                        radius = radius,
                                        width = constraints.maxWidth,
                                        height = constraints.maxHeight,
                                    )
                                ballClick()
                            }
                        }
                    },
        ) {
            drawCircle(
                color = ballColor,
                radius = radius,
                center = ballPosition,
            )
        }
    }
}

private fun randomOffset(
    radius: Float,
    width: Int,
    height: Int,
) = Offset(
    x = Random.nextInt(radius.roundToInt(), width - radius.roundToInt()).toFloat(),
    y = Random.nextInt(radius.roundToInt(), height - radius.roundToInt()).toFloat(),
)

@Preview
@Composable
private fun BallClickerPreview() {
    val state =
        BallClickerState(
            points = 3,
            currentTimeInSeconds = 20,
            isTimerRunning = false,
        )
    BallClickerScreen(state = state, onEvent = {})
}
