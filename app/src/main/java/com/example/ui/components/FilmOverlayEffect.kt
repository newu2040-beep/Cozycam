package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.FilmProcessingParams
import com.example.ui.theme.CozyFilmDateAmber
import java.util.Random

@Composable
fun FilmOverlayEffect(
  params: FilmProcessingParams,
  showGrid: Boolean = true,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // 1. Temperature & Tint color wash
      if (params.temperature != 0f || params.tint != 0f) {
        val washColor = when {
          params.temperature > 0.1f -> Color(0xFFFFA500).copy(alpha = (params.temperature * 0.12f).coerceIn(0f, 0.25f))
          params.temperature < -0.1f -> Color(0xFF64B5F6).copy(alpha = (-params.temperature * 0.12f).coerceIn(0f, 0.25f))
          params.tint < -0.1f -> Color(0xFF81C784).copy(alpha = (-params.tint * 0.1f).coerceIn(0f, 0.2f))
          else -> Color.Transparent
        }
        drawRect(color = washColor)
      }

      // 2. Light leak flare on corner if preset has light leak
      if (params.lightLeak > 0.05f) {
        drawRect(
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0x99FFA028),
              Color(0x44FF5722),
              Color.Transparent
            ),
            start = Offset(w, 0f),
            end = Offset(w * 0.4f, h * 0.6f)
          )
        )
      }

      // 3. Vignette shading
      if (params.vignette > 0.05f) {
        val vignetteAlpha = (params.vignette * 0.5f).coerceIn(0f, 0.8f)
        drawRect(
          brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0x00000000), Color(0xFF100E0C).copy(alpha = vignetteAlpha)),
            center = Offset(w / 2f, h / 2f),
            radius = maxOf(w, h) * 0.7f
          )
        )
      }

      // 4. Rule of thirds grid lines
      if (showGrid) {
        val gridColor = Color(0x33FFFFFF)
        val strokeW = 1.2f

        // Horizontal lines (1/3 and 2/3)
        drawLine(
          color = gridColor,
          start = Offset(0f, h / 3f),
          end = Offset(w, h / 3f),
          strokeWidth = strokeW
        )
        drawLine(
          color = gridColor,
          start = Offset(0f, h * 2f / 3f),
          end = Offset(w, h * 2f / 3f),
          strokeWidth = strokeW
        )

        // Vertical lines (1/3 and 2/3)
        drawLine(
          color = gridColor,
          start = Offset(w / 3f, 0f),
          end = Offset(w / 3f, h),
          strokeWidth = strokeW
        )
        drawLine(
          color = gridColor,
          start = Offset(w * 2f / 3f, 0f),
          end = Offset(w * 2f / 3f, h),
          strokeWidth = strokeW
        )

        // Center crosshair / focus bracket
        val center = Offset(w / 2f, h / 2f)
        val bracketSize = 18f
        val bracketColor = Color(0x4DFFFFFF)
        drawLine(color = bracketColor, start = Offset(center.x - bracketSize, center.y), end = Offset(center.x + bracketSize, center.y), strokeWidth = 1.2f)
        drawLine(color = bracketColor, start = Offset(center.x, center.y - bracketSize), end = Offset(center.x, center.y + bracketSize), strokeWidth = 1.2f)
      }
    }

    // 5. Authentic Retro Quartz Date Stamp (SEP 16 2026)
    if (params.dateStampEnabled) {
      Text(
        text = params.dateStampText,
        color = CozyFilmDateAmber,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 2.5.sp,
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(start = 28.dp, bottom = 120.dp)
      )
    }
  }
}
