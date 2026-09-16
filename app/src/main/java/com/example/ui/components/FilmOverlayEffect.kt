package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.FilmPreset
import com.example.domain.models.FilmProcessingParams
import com.example.ui.theme.CozyFilmDateAmber
import java.util.Random

@Composable
fun FilmOverlayEffect(
  params: FilmProcessingParams,
  preset: FilmPreset? = null,
  showGrid: Boolean = true,
  modifier: Modifier = Modifier
) {
  // Pre-generate pseudo-random film grain and dust dots for canvas drawing
  val grainDots = remember {
    val rng = Random(42)
    List(180) {
      Triple(rng.nextFloat(), rng.nextFloat(), rng.nextFloat() * 1.8f + 0.6f)
    }
  }

  val dustSpecks = remember {
    val rng = Random(1337)
    List(12) {
      Offset(rng.nextFloat(), rng.nextFloat())
    }
  }

  val isMonochrome = (preset?.category == "B&W") ||
    (preset?.id?.contains("mono", ignoreCase = true) == true) ||
    (params.saturation <= 0.05f)

  Box(modifier = modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // 1. MONOCHROME / BLACK & WHITE LIVE FILTER
      // Using BlendMode.Color with neutral gray #808080 desaturates the camera TextureView in real-time!
      if (isMonochrome) {
        drawRect(
          color = Color(0xFF808080),
          blendMode = BlendMode.Color
        )
        // Enhance B&W film contrast and silver tones
        drawRect(
          color = Color(0xFF101010).copy(alpha = 0.22f),
          blendMode = BlendMode.Overlay
        )
      } else {
        // 2. PRESET-SPECIFIC VINTAGE COLOR PROFILES
        val presetId = preset?.id ?: ""
        when {
          presetId == "super_8mm" || presetId.contains("super8", ignoreCase = true) -> {
            // Golden vintage 1970s sepia / tungsten glow
            drawRect(
              color = Color(0xFFE5A038).copy(alpha = 0.32f),
              blendMode = BlendMode.Color
            )
            drawRect(
              color = Color(0xFF5D4037).copy(alpha = 0.16f),
              blendMode = BlendMode.Multiply
            )
          }
          presetId == "kodak_gold" || presetId.contains("gold", ignoreCase = true) -> {
            // Rich golden-hour warmth & vibrant saturation
            drawRect(
              color = Color(0xFFFFA000).copy(alpha = 0.26f),
              blendMode = BlendMode.Color
            )
            drawRect(
              color = Color(0xFFFFECB3).copy(alpha = 0.14f),
              blendMode = BlendMode.Softlight
            )
          }
          presetId == "fuji_c200" || presetId.contains("fuji", ignoreCase = true) -> {
            // Cool emerald tones and soft pastel greens
            drawRect(
              color = Color(0xFF26A69A).copy(alpha = 0.20f),
              blendMode = BlendMode.Color
            )
          }
          presetId == "cinema_teal_amber" || presetId.contains("cinema", ignoreCase = true) -> {
            // Teal shadows & warm amber highlights
            drawRect(
              brush = Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF00838F).copy(alpha = 0.22f),
                  Color(0xFFFFB300).copy(alpha = 0.18f)
                )
              ),
              blendMode = BlendMode.Color
            )
            drawRect(
              color = Color(0xFF00363A).copy(alpha = 0.16f),
              blendMode = BlendMode.Overlay
            )
          }
          presetId == "vhs_tape" || presetId.contains("vhs", ignoreCase = true) -> {
            // Magenta / purple retro shift with slight contrast lift
            drawRect(
              color = Color(0xFFAB47BC).copy(alpha = 0.18f),
              blendMode = BlendMode.Color
            )
            // VHS horizontal scanline pattern
            val scanlineCount = 45
            val stepY = h / scanlineCount
            for (i in 0 until scanlineCount) {
              if (i % 2 == 0) {
                drawLine(
                  color = Color(0x22000000),
                  start = Offset(0f, i * stepY),
                  end = Offset(w, i * stepY),
                  strokeWidth = 1.0f
                )
              }
            }
          }
          presetId == "ccd_digital" || presetId.contains("ccd", ignoreCase = true) -> {
            // Early 2000s cool blue point-and-shoot digital tone
            drawRect(
              color = Color(0xFF42A5F5).copy(alpha = 0.16f),
              blendMode = BlendMode.Color
            )
            drawRect(
              color = Color.White.copy(alpha = 0.10f),
              blendMode = BlendMode.Overlay
            )
          }
          presetId == "disposable" || presetId.contains("disposable", ignoreCase = true) -> {
            // Punchy flash saturation with warm plastic lens cast
            drawRect(
              color = Color(0xFFFFD54F).copy(alpha = 0.24f),
              blendMode = BlendMode.Color
            )
            drawRect(
              color = Color.Black.copy(alpha = 0.15f),
              blendMode = BlendMode.Overlay
            )
          }
          presetId == "polaroid_instant" || presetId.contains("polaroid", ignoreCase = true) -> {
            // Lifted creamy shadows and dreamy softglow
            drawRect(
              color = Color(0xFFFFF3E0).copy(alpha = 0.18f),
              blendMode = BlendMode.Screen
            )
          }
          presetId == "classic_35mm" -> {
            // Classic analog warm negative
            drawRect(
              color = Color(0xFFFFB74D).copy(alpha = 0.18f),
              blendMode = BlendMode.Color
            )
          }
          else -> {
            // Generic warm/cool grading based on preset temperature
            if (params.temperature > 0.05f) {
              drawRect(
                color = Color(0xFFFFA000).copy(alpha = (params.temperature * 0.25f).coerceIn(0f, 0.35f)),
                blendMode = BlendMode.Color
              )
            } else if (params.temperature < -0.05f) {
              drawRect(
                color = Color(0xFF29B6F6).copy(alpha = (-params.temperature * 0.25f).coerceIn(0f, 0.35f)),
                blendMode = BlendMode.Color
              )
            }
          }
        }
      }

      // 3. LIVE TEMPERATURE SLIDER OVERLAY (User manual adjustment)
      if (params.temperature > 0.05f) {
        drawRect(
          color = Color(0xFFFF8F00).copy(alpha = (params.temperature * 0.32f).coerceIn(0f, 0.45f)),
          blendMode = BlendMode.Color
        )
      } else if (params.temperature < -0.05f) {
        drawRect(
          color = Color(0xFF0288D1).copy(alpha = (-params.temperature * 0.32f).coerceIn(0f, 0.45f)),
          blendMode = BlendMode.Color
        )
      }

      // 4. LIVE EXPOSURE SLIDER OVERLAY (User manual adjustment)
      if (params.exposure > 0.05f) {
        // High-key highlight lift
        drawRect(
          color = Color.White.copy(alpha = (params.exposure * 0.42f).coerceIn(0f, 0.50f)),
          blendMode = BlendMode.Screen
        )
      } else if (params.exposure < -0.05f) {
        // Low-key shadow darkening
        drawRect(
          color = Color.Black.copy(alpha = (-params.exposure * 0.50f).coerceIn(0f, 0.60f)),
          blendMode = BlendMode.Multiply
        )
      }

      // 5. LIVE FILM GRAIN & SPECKS (Simulated analog emulsion grain)
      if (params.grain > 0.1f) {
        val grainAlpha = (params.grain * 0.25f).coerceIn(0.04f, 0.30f)
        for (dot in grainDots) {
          drawCircle(
            color = Color.White.copy(alpha = grainAlpha * 0.7f),
            radius = dot.third,
            center = Offset(dot.first * w, dot.second * h)
          )
        }
        for (speck in dustSpecks) {
          drawCircle(
            color = Color.Black.copy(alpha = grainAlpha),
            radius = 1.2f,
            center = Offset(speck.x * w, speck.y * h)
          )
        }
      }

      // 6. LIGHT LEAK EFFECT (Warm red-amber streak from top-right)
      if (params.lightLeak > 0.05f) {
        val leakAlpha = (params.lightLeak * 0.85f).coerceIn(0f, 0.80f)
        drawRect(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFFF5722).copy(alpha = leakAlpha),
              Color(0xFFFF9800).copy(alpha = leakAlpha * 0.6f),
              Color.Transparent
            ),
            center = Offset(w * 0.95f, h * 0.05f),
            radius = maxOf(w, h) * 0.65f
          ),
          blendMode = BlendMode.Screen
        )
      }

      // 7. HALATION GLOW (Soft warm aura along outer border)
      if (params.halation > 0.1f) {
        val halationAlpha = (params.halation * 0.35f).coerceIn(0f, 0.45f)
        drawRect(
          brush = Brush.radialGradient(
            colors = listOf(
              Color.Transparent,
              Color(0xFFFF3D00).copy(alpha = halationAlpha)
            ),
            center = Offset(w / 2f, h / 2f),
            radius = maxOf(w, h) * 0.72f
          ),
          blendMode = BlendMode.Screen
        )
      }

      // 8. VIGNETTE SHADING (Darkened edges and corners)
      if (params.vignette > 0.05f) {
        val vignetteAlpha = (params.vignette * 0.75f).coerceIn(0f, 0.85f)
        drawRect(
          brush = Brush.radialGradient(
            colors = listOf(
              Color.Transparent,
              Color(0x00000000),
              Color(0xFF0A0908).copy(alpha = vignetteAlpha)
            ),
            center = Offset(w / 2f, h / 2f),
            radius = maxOf(w, h) * 0.68f
          )
        )
      }

      // 9. 35MM SPROCKETS OR POLAROID BORDERS
      if (params.frameStyle == "film35mm") {
        // Draw top and bottom 35mm film borders with sprocket perforation holes
        val borderHeight = h * 0.08f
        drawRect(color = Color(0xFF141210), size = Size(w, borderHeight))
        drawRect(color = Color(0xFF141210), topLeft = Offset(0f, h - borderHeight), size = Size(w, borderHeight))

        val sprocketWidth = 14f
        val sprocketHeight = 10f
        val sprocketSpacing = 32f
        var curX = 16f
        while (curX < w - 16f) {
          drawRoundRect(
            color = Color(0xFF2C2825),
            topLeft = Offset(curX, (borderHeight - sprocketHeight) / 2f),
            size = Size(sprocketWidth, sprocketHeight),
            cornerRadius = CornerRadius(3f, 3f)
          )
          drawRoundRect(
            color = Color(0xFF2C2825),
            topLeft = Offset(curX, h - borderHeight + (borderHeight - sprocketHeight) / 2f),
            size = Size(sprocketWidth, sprocketHeight),
            cornerRadius = CornerRadius(3f, 3f)
          )
          curX += sprocketSpacing
        }
      } else if (params.frameStyle == "polaroid") {
        // Draw Polaroid bottom frame chin
        val chinHeight = h * 0.12f
        drawRect(
          color = Color(0xFFF7F5F0),
          topLeft = Offset(0f, h - chinHeight),
          size = Size(w, chinHeight)
        )
      }

      // 10. RULE OF THIRDS GRID & CENTER BRACKET
      if (showGrid) {
        val gridColor = Color(0x38FFFFFF)
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
        val bracketSize = 16f
        val bracketColor = Color(0x55FFFFFF)
        drawLine(
          color = bracketColor,
          start = Offset(center.x - bracketSize, center.y),
          end = Offset(center.x + bracketSize, center.y),
          strokeWidth = 1.2f
        )
        drawLine(
          color = bracketColor,
          start = Offset(center.x, center.y - bracketSize),
          end = Offset(center.x, center.y + bracketSize),
          strokeWidth = 1.2f
        )
      }
    }

    // 11. AUTHENTIC RETRO QUARTZ DATE STAMP (SEP 16 2026)
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
          .padding(start = 24.dp, bottom = 44.dp)
      )
    }
  }
}
