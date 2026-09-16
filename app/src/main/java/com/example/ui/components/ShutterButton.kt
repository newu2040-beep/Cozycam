package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShutterButton(
  isVideoMode: Boolean = false,
  isRecording: Boolean = false,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 82.dp
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "shutter_scale")

  Box(
    modifier = modifier
      .size(size)
      .scale(scale)
      .testTag("shutter_button")
      .clickable(
        interactionSource = interactionSource,
        indication = ripple(bounded = false, radius = size / 2),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height
      val center = Offset(w / 2f, h / 2f)
      val outerRadius = w / 2f - 3f

      // Outer bezel ring (brushed dark metal)
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFF383531), Color(0xFF1E1C1A)),
          center = center,
          radius = outerRadius
        ),
        radius = outerRadius,
        center = center,
        style = Stroke(width = 4f)
      )

      // Outer gap background
      drawCircle(
        color = Color(0x66000000),
        radius = outerRadius - 3f,
        center = center
      )

      val innerRadius = outerRadius - 10f

      if (isVideoMode) {
        if (isRecording) {
          // Recording active: Red rounded square
          val squareSize = innerRadius * 1.1f
          drawRoundRect(
            color = Color(0xFFFF3B30),
            topLeft = Offset(center.x - squareSize / 2f, center.y - squareSize / 2f),
            size = Size(squareSize, squareSize),
            cornerRadius = CornerRadius(8f, 8f)
          )
        } else {
          // Video ready: Red circular core
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFFFF5252), Color(0xFFD32F2F)),
              center = center,
              radius = innerRadius
            ),
            radius = innerRadius,
            center = center
          )
        }
      } else {
        // Photo Shutter: Ivory/Warm Cream Vintage Button
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF2ECE1), Color(0xFFDFD7C7)),
            center = Offset(center.x - 3f, center.y - 4f),
            radius = innerRadius
          ),
          radius = innerRadius,
          center = center
        )

        // Subtle inner ring bevel
        drawCircle(
          color = Color(0x33B5AB99),
          radius = innerRadius * 0.88f,
          center = center,
          style = Stroke(width = 1.5f)
        )
      }
    }
  }
}
