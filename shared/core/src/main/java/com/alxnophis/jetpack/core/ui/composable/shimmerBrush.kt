package com.alxnophis.jetpack.core.ui.composable

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.theme.AppTheme

@Immutable
data class ShimmerColors(
    val colors: List<Color> =
        listOf(
            Color.LightGray.copy(alpha = 0.5f),
            Color.LightGray,
            Color.LightGray.copy(alpha = 0.5f),
        ),
)

@Composable
fun shimmerBrush(
    showShimmer: Boolean = true,
    shimmerColors: ShimmerColors = ShimmerColors(),
    targetValue: Float = 1200f,
): Brush =
    if (showShimmer) {
        val transition = rememberInfiniteTransition(label = "shimmerTransition")
        val translateAnimation =
            transition.animateFloat(
                label = "shimmerAnimation",
                initialValue = 0f,
                targetValue = targetValue,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(1200),
                        repeatMode = RepeatMode.Restart,
                    ),
            )
        Brush.linearGradient(
            colors = shimmerColors.colors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value),
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero,
        )
    }

@Preview
@Composable
private fun ShimmerBrushPreview() {
    AppTheme {
        val height = 20.dp
        val roundedCornersShape = RoundedCornerShape(25)
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
            verticalArrangement = Arrangement.Absolute.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .width(150.dp)
                        .height(height)
                        .clip(roundedCornersShape)
                        .background(shimmerBrush()),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(height)
                        .clip(roundedCornersShape)
                        .background(shimmerBrush()),
            )
        }
    }
}
