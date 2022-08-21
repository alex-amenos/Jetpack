package com.alxnophis.jetpack.game.ballclicker.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.game.ballclicker.R
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerEvent
import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerState
import com.alxnophis.jetpack.game.ballclicker.ui.viewmodel.BallClickerViewModel
import com.alxnophis.jetpack.kotlin.constants.WHITE_SPACE
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random
import org.koin.androidx.compose.getViewModel

@Composable
internal fun BallClickerScreen(
    navController: NavController,
    viewModel: BallClickerViewModel = getViewModel()
) {
    BackHandler {
        viewModel
            .handleEvent(BallClickerEvent.Stop)
            .also { navController.popBackStack() }
    }
    BallClickerContent(
        viewModel.uiState.collectAsState().value,
        viewModel::handleEvent
    )
}

@Composable
internal fun BallClickerContent(
    state: BallClickerState,
    handleEvent: BallClickerEvent.() -> Unit
) {
    CoreTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildString {
                        append(stringResource(R.string.ball_clicker_points))
                        append(WHITE_SPACE)
                        append(state.points)
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.currentTimeInSeconds.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = {
                        if (state.isTimerRunning) {
                            handleEvent(BallClickerEvent.Stop)
                        } else {
                            handleEvent(BallClickerEvent.Start)
                        }
                    }
                ) {
                    Text(
                        text = when {
                            state.isTimerRunning -> stringResource(R.string.ball_clicker_reset)
                            else -> stringResource(R.string.ball_clicker_start)
                        }
                    )
                }
            }
            BallClickerContent(enabled = state.isTimerRunning) {
                handleEvent(BallClickerEvent.BallClicked)
            }
        }
    }
}

@Composable
private fun BallClickerContent(
    radius: Float = 100f,
    enabled: Boolean = false,
    ballColor: Color = MaterialTheme.colors.secondary,
    ballClick: () -> Unit = {}
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        var ballPosition by remember {
            mutableStateOf(
                randomOffset(
                    radius = radius,
                    width = constraints.maxWidth,
                    height = constraints.maxHeight
                )
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(enabled) {
                    if (!enabled) {
                        return@pointerInput
                    }
                    detectTapGestures {
                        val distance = sqrt((it.x - ballPosition.x).pow(2) + (it.y - ballPosition.y).pow(2))
                        if (distance <= radius) {
                            ballPosition = randomOffset(
                                radius = radius,
                                width = constraints.maxWidth,
                                height = constraints.maxHeight
                            )
                            ballClick()
                        }
                    }
                }
        ) {
            drawCircle(
                color = ballColor,
                radius = radius,
                center = ballPosition
            )
        }
    }
}

private fun randomOffset(radius: Float, width: Int, height: Int): Offset {
    return Offset(
        x = Random.nextInt(radius.roundToInt(), width - radius.roundToInt()).toFloat(),
        y = Random.nextInt(radius.roundToInt(), height - radius.roundToInt()).toFloat()
    )
}

@Preview
@Composable
private fun BallClickerPreview() {
    BallClickerContent()
}
