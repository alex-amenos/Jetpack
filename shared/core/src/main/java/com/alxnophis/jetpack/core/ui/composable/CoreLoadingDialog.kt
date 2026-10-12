package com.alxnophis.jetpack.core.ui.composable

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.alxnophis.jetpack.core.ui.theme.AppTheme

private const val THREE_DOTS_ANIMATION_DURATION = 1200
private const val THREE_DOTS_STEP_DELAY = 200
private const val THREE_DOTS_PULSE_DURATION = 500
private const val THREE_DOTS_MIN_SCALE = 0.5f
private const val THREE_DOTS_MAX_SCALE = 1f
private const val NUMBER_OF_DOTS = 3

@Composable
@SuppressLint("ComposeModifierMissing")
fun CoreLoadingDialog(
    isLoading: Boolean,
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false,
) {
    if (isLoading) {
        Dialog(
            onDismissRequest = {},
            properties =
                DialogProperties(
                    dismissOnBackPress = dismissOnBackPress,
                    dismissOnClickOutside = dismissOnClickOutside,
                ),
        ) {
            CoreLoadingContent(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun CoreLoadingContent(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 20.dp,
    spaceBetween: Dp = 16.dp,
    animationDuration: Int = THREE_DOTS_ANIMATION_DURATION,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ThreeDotsLoadingTransition")
    val dotScales =
        List(NUMBER_OF_DOTS) { index ->
            val start = index * THREE_DOTS_STEP_DELAY
            val peak = start + THREE_DOTS_PULSE_DURATION / 2
            val end = start + THREE_DOTS_PULSE_DURATION

            infiniteTransition.animateFloat(
                initialValue = THREE_DOTS_MIN_SCALE,
                targetValue = THREE_DOTS_MIN_SCALE,
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            keyframes {
                                this.durationMillis = animationDuration
                                THREE_DOTS_MIN_SCALE at 0
                                if (start > 0) {
                                    THREE_DOTS_MIN_SCALE at start
                                }
                                THREE_DOTS_MAX_SCALE at peak using FastOutSlowInEasing
                                THREE_DOTS_MIN_SCALE at end using FastOutSlowInEasing
                                THREE_DOTS_MIN_SCALE at animationDuration
                            },
                        repeatMode = RepeatMode.Restart,
                    ),
                label = "DotScaleAnimation$index",
            )
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spaceBetween),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dotScales.forEach { scaleState ->
                Box(
                    modifier =
                        Modifier
                            .size(dotSize)
                            .graphicsLayer {
                                scaleX = scaleState.value
                                scaleY = scaleState.value
                            }.background(
                                color = color,
                                shape = CircleShape,
                            ),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CoreLoadingDialogPreview() {
    AppTheme {
        Surface {
            CoreLoadingDialog(isLoading = true)
        }
    }
}

@PreviewLightDark
@Composable
private fun CoreLoadingContentPreview() {
    AppTheme {
        Surface {
            CoreLoadingContent(
                modifier =
                    Modifier
                        .width(300.dp)
                        .padding(32.dp),
            )
        }
    }
}
