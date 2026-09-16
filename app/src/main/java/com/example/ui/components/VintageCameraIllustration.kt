package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.models.CameraIllustrationType

@Composable
fun VintageCameraIllustration(
  type: CameraIllustrationType,
  modifier: Modifier = Modifier,
  size: Dp = 100.dp
) {
  Box(modifier = modifier.size(size)) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      when (type) {
        CameraIllustrationType.RANGEFINDER_35MM -> drawRangefinder(this)
        CameraIllustrationType.KODAK_GOLD -> drawKodakCamera(this)
        CameraIllustrationType.FUJI_COMPACT -> drawFujiCamera(this)
        CameraIllustrationType.PORTRA_CHROME -> drawPortraChrome(this)
        CameraIllustrationType.DISPOSABLE_YELLOW -> drawDisposableCamera(this)
        CameraIllustrationType.CCD_DIGITAL -> drawCcdDigital(this)
        CameraIllustrationType.VHS_CAMCORDER -> drawVhsCamcorder(this)
        CameraIllustrationType.POLAROID_INSTANT -> drawPolaroidInstant(this)
        CameraIllustrationType.MONO_SLR -> drawMonoSlr(this)
        CameraIllustrationType.CINEMA_CAMERA -> drawCinemaCamera(this)
      }
    }
  }
}

// 1. Classic 35mm Rangefinder (Chrome top, textured black body, glass lens)
private fun drawRangefinder(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height

  // Main body
  val bodyTop = h * 0.28f
  val bodyH = h * 0.58f
  val bodyW = w * 0.88f
  val bodyX = (w - bodyW) / 2

  // Textured black leatherette lower body
  scope.drawRoundRect(
    color = Color(0xFF22201E),
    topLeft = Offset(bodyX, bodyTop + bodyH * 0.25f),
    size = Size(bodyW, bodyH * 0.75f),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // Brushed chrome top plate
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFE2DFDC), Color(0xFFB8B4AF), Color(0xFF94908B))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH * 0.28f),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // Shutter button & dials on top plate
  scope.drawRoundRect(
    color = Color(0xFFD4CFC9),
    topLeft = Offset(bodyX + bodyW * 0.72f, bodyTop - h * 0.06f),
    size = Size(bodyW * 0.14f, h * 0.08f),
    cornerRadius = CornerRadius(4f, 4f)
  )
  scope.drawRoundRect(
    color = Color(0xFFA5A09A),
    topLeft = Offset(bodyX + bodyW * 0.15f, bodyTop - h * 0.04f),
    size = Size(bodyW * 0.16f, h * 0.06f),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // Viewfinder window
  scope.drawRoundRect(
    color = Color(0xFF1E262B),
    topLeft = Offset(bodyX + bodyW * 0.72f, bodyTop + bodyH * 0.06f),
    size = Size(bodyW * 0.12f, bodyH * 0.14f),
    cornerRadius = CornerRadius(3f, 3f)
  )

  // Central Rangefinder Lens
  val lensCenter = Offset(w * 0.5f, bodyTop + bodyH * 0.55f)
  val lensRadius = bodyH * 0.36f

  // Outer chrome ring
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFEDEDED), Color(0xFF7A7875)),
      center = lensCenter,
      radius = lensRadius
    ),
    radius = lensRadius,
    center = lensCenter
  )
  // Inner black barrel
  scope.drawCircle(
    color = Color(0xFF141312),
    radius = lensRadius * 0.82f,
    center = lensCenter
  )
  // Glass element with amber & cyan reflection
  scope.drawCircle(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFFE5A856), Color(0xFF153344), Color(0xFF0D1217)),
      start = Offset(lensCenter.x - lensRadius, lensCenter.y - lensRadius),
      end = Offset(lensCenter.x + lensRadius, lensCenter.y + lensRadius)
    ),
    radius = lensRadius * 0.65f,
    center = lensCenter
  )
  // Center aperture reflection
  scope.drawCircle(
    color = Color(0x99FFFFFF),
    radius = lensRadius * 0.16f,
    center = Offset(lensCenter.x - lensRadius * 0.2f, lensCenter.y - lensRadius * 0.2f)
  )
}

// 2. Kodak Gold Vintage Camera (Warm golden yellow body)
private fun drawKodakCamera(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.28f
  val bodyH = h * 0.58f
  val bodyW = w * 0.88f
  val bodyX = (w - bodyW) / 2

  // Golden Yellow Body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFF3C044), Color(0xFFE2A829), Color(0xFFC48B15))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH),
    cornerRadius = CornerRadius(16f, 16f)
  )

  // Black accent grip & top
  scope.drawRoundRect(
    color = Color(0xFF221F1B),
    topLeft = Offset(bodyX + bodyW * 0.05f, bodyTop + bodyH * 0.15f),
    size = Size(bodyW * 0.22f, bodyH * 0.7f),
    cornerRadius = CornerRadius(8f, 8f)
  )

  // Flash module
  scope.drawRoundRect(
    color = Color(0xFFEFEBE4),
    topLeft = Offset(bodyX + bodyW * 0.68f, bodyTop + bodyH * 0.1f),
    size = Size(bodyW * 0.22f, bodyH * 0.18f),
    cornerRadius = CornerRadius(4f, 4f)
  )
  scope.drawRoundRect(
    color = Color(0xFFD48B10),
    topLeft = Offset(bodyX + bodyW * 0.72f, bodyTop - h * 0.04f),
    size = Size(bodyW * 0.14f, h * 0.06f),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // Center Lens
  val lensCenter = Offset(w * 0.52f, bodyTop + bodyH * 0.52f)
  val lensRadius = bodyH * 0.32f
  scope.drawCircle(color = Color(0xFF252320), radius = lensRadius, center = lensCenter)
  scope.drawCircle(color = Color(0xFF3D3833), radius = lensRadius * 0.85f, center = lensCenter)
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFFFAE33), Color(0xFF1E2833)),
      center = lensCenter,
      radius = lensRadius * 0.6f
    ),
    radius = lensRadius * 0.62f,
    center = lensCenter
  )
}

// 3. Fuji C200 Forest Green Compact Camera
private fun drawFujiCamera(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.30f
  val bodyH = h * 0.54f
  val bodyW = w * 0.88f
  val bodyX = (w - bodyW) / 2

  // Green Enamel Body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF5A8B6F), Color(0xFF3F6952), Color(0xFF2D4E3C))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // Silver metal top strip
  scope.drawRoundRect(
    color = Color(0xFFD6D3CD),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH * 0.2f),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // Lens
  val lensCenter = Offset(w * 0.48f, bodyTop + bodyH * 0.56f)
  val lensRadius = bodyH * 0.3f
  scope.drawCircle(color = Color(0xFF314337), radius = lensRadius, center = lensCenter)
  scope.drawCircle(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFF70B88F), Color(0xFF1B2F23)),
      start = Offset(lensCenter.x - lensRadius, lensCenter.y - lensRadius),
      end = Offset(lensCenter.x + lensRadius, lensCenter.y + lensRadius)
    ),
    radius = lensRadius * 0.7f,
    center = lensCenter
  )
}

// 4. Portra 400 Chrome Rangefinder
private fun drawPortraChrome(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.28f
  val bodyH = h * 0.58f
  val bodyW = w * 0.88f
  val bodyX = (w - bodyW) / 2

  // Pure brushed chrome body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFF0EFEB), Color(0xFFCDC9C1), Color(0xFFA19C93))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // Charcoal middle band
  scope.drawRect(
    color = Color(0xFF3A3734),
    topLeft = Offset(bodyX, bodyTop + bodyH * 0.28f),
    size = Size(bodyW, bodyH * 0.52f)
  )

  val lensCenter = Offset(w * 0.5f, bodyTop + bodyH * 0.54f)
  val lensRadius = bodyH * 0.33f
  scope.drawCircle(color = Color(0xFFE0DDD7), radius = lensRadius, center = lensCenter)
  scope.drawCircle(color = Color(0xFF1E1D1C), radius = lensRadius * 0.8f, center = lensCenter)
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF4FA0C2), Color(0xFF121C24)),
      center = lensCenter,
      radius = lensRadius * 0.6f
    ),
    radius = lensRadius * 0.6f,
    center = lensCenter
  )
}

// 5. Disposable Camera (Raw, fun yellow plastic with flash window)
private fun drawDisposableCamera(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.28f
  val bodyH = h * 0.58f
  val bodyW = w * 0.88f
  val bodyX = (w - bodyW) / 2

  // Plastic yellow body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFFCD035), Color(0xFFDEAA16))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH),
    cornerRadius = CornerRadius(16f, 16f)
  )

  // Flash ready bulb & window
  scope.drawRoundRect(
    color = Color(0xFFEEEEEE),
    topLeft = Offset(bodyX + bodyW * 0.1f, bodyTop + bodyH * 0.15f),
    size = Size(bodyW * 0.25f, bodyH * 0.25f),
    cornerRadius = CornerRadius(6f, 6f)
  )
  scope.drawCircle(
    color = Color(0xFFFF453A),
    radius = 5f,
    center = Offset(bodyX + bodyW * 0.42f, bodyTop + bodyH * 0.25f)
  )

  // Small plastic lens
  val lensCenter = Offset(bodyX + bodyW * 0.68f, bodyTop + bodyH * 0.52f)
  val lensRadius = bodyH * 0.26f
  scope.drawCircle(color = Color(0xFF22201D), radius = lensRadius, center = lensCenter)
  scope.drawCircle(color = Color(0xFF47433E), radius = lensRadius * 0.65f, center = lensCenter)
  scope.drawCircle(color = Color(0xFF111111), radius = lensRadius * 0.35f, center = lensCenter)
}

// 6. CCD Early Digital Camera (2000s silver pocket camera)
private fun drawCcdDigital(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.30f
  val bodyH = h * 0.54f
  val bodyW = w * 0.86f
  val bodyX = (w - bodyW) / 2

  scope.drawRoundRect(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFFDDE3EA), Color(0xFFB5BFCB), Color(0xFF919CA8))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH),
    cornerRadius = CornerRadius(20f, 20f)
  )

  // Circular lens barrel housing
  val lensCenter = Offset(w * 0.45f, bodyTop + bodyH * 0.5f)
  val lensRadius = bodyH * 0.34f
  scope.drawCircle(color = Color(0xFF8B96A4), radius = lensRadius, center = lensCenter)
  scope.drawCircle(color = Color(0xFFE8EEF5), radius = lensRadius * 0.88f, center = lensCenter)
  scope.drawCircle(color = Color(0xFF2B333C), radius = lensRadius * 0.68f, center = lensCenter)

  // Lens glass reflection
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF5BA4E6), Color(0xFF0F1A24)),
      center = lensCenter,
      radius = lensRadius * 0.5f
    ),
    radius = lensRadius * 0.5f,
    center = lensCenter
  )
}

// 7. VHS Camcorder (80s shoulder/handheld video recorder)
private fun drawVhsCamcorder(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.32f
  val bodyH = h * 0.48f
  val bodyW = w * 0.90f
  val bodyX = (w - bodyW) / 2

  // Dark matte polymer camcorder body
  scope.drawRoundRect(
    color = Color(0xFF262428),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW * 0.68f, bodyH),
    cornerRadius = CornerRadius(12f, 12f)
  )

  // Cassette tape window
  scope.drawRoundRect(
    color = Color(0xFF141316),
    topLeft = Offset(bodyX + bodyW * 0.1f, bodyTop + bodyH * 0.22f),
    size = Size(bodyW * 0.45f, bodyH * 0.52f),
    cornerRadius = CornerRadius(4f, 4f)
  )
  // Two tape reels
  scope.drawCircle(color = Color(0xFF5A5860), radius = 10f, center = Offset(bodyX + bodyW * 0.22f, bodyTop + bodyH * 0.48f))
  scope.drawCircle(color = Color(0xFF5A5860), radius = 10f, center = Offset(bodyX + bodyW * 0.43f, bodyTop + bodyH * 0.48f))

  // Extended zoom lens cone on the right
  scope.drawRoundRect(
    color = Color(0xFF1C1A1E),
    topLeft = Offset(bodyX + bodyW * 0.68f, bodyTop + bodyH * 0.18f),
    size = Size(bodyW * 0.28f, bodyH * 0.64f),
    cornerRadius = CornerRadius(8f, 8f)
  )
  // Red Recording indicator dot
  scope.drawCircle(color = Color(0xFFFF3B30), radius = 6f, center = Offset(bodyX + bodyW * 0.15f, bodyTop + bodyH * 0.12f))
}

// 8. Polaroid Instant Camera (Iconic vintage white box with rainbow stripe)
private fun drawPolaroidInstant(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.25f
  val bodyH = h * 0.62f
  val bodyW = w * 0.82f
  val bodyX = (w - bodyW) / 2

  // Cream white retro body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFFAF7F0), Color(0xFFEBE5D8), Color(0xFFD8D0BF))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH),
    cornerRadius = CornerRadius(18f, 18f)
  )

  // Dark front plate
  scope.drawRoundRect(
    color = Color(0xFF2C2825),
    topLeft = Offset(bodyX + bodyW * 0.08f, bodyTop + bodyH * 0.12f),
    size = Size(bodyW * 0.84f, bodyH * 0.55f),
    cornerRadius = CornerRadius(10f, 10f)
  )

  // Central large instant lens
  val lensCenter = Offset(w * 0.5f, bodyTop + bodyH * 0.38f)
  val lensRadius = bodyH * 0.24f
  scope.drawCircle(color = Color(0xFF141312), radius = lensRadius, center = lensCenter)
  scope.drawCircle(color = Color(0xFF423E3A), radius = lensRadius * 0.8f, center = lensCenter)
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFE5A856), Color(0xFF192531)),
      center = lensCenter,
      radius = lensRadius * 0.55f
    ),
    radius = lensRadius * 0.55f,
    center = lensCenter
  )

  // Red shutter button on side
  scope.drawCircle(
    color = Color(0xFFE53935),
    radius = 12f,
    center = Offset(bodyX + bodyW * 0.2f, bodyTop + bodyH * 0.8f)
  )

  // Ejection slot at bottom
  scope.drawRoundRect(
    color = Color(0xFF24201D),
    topLeft = Offset(bodyX + bodyW * 0.15f, bodyTop + bodyH * 0.92f),
    size = Size(bodyW * 0.7f, 6f),
    cornerRadius = CornerRadius(3f, 3f)
  )
}

// 9. Monochrome SLR
private fun drawMonoSlr(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.30f
  val bodyH = h * 0.54f
  val bodyW = w * 0.88f
  val bodyX = (w - bodyW) / 2

  // Pentaprism bump on top
  scope.drawRoundRect(
    color = Color(0xFF1E1D1C),
    topLeft = Offset(w * 0.38f, bodyTop - h * 0.1f),
    size = Size(w * 0.24f, h * 0.14f),
    cornerRadius = CornerRadius(6f, 6f)
  )

  // Matte black body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF33312F), Color(0xFF1A1918))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // Lens
  val lensCenter = Offset(w * 0.5f, bodyTop + bodyH * 0.52f)
  val lensRadius = bodyH * 0.34f
  scope.drawCircle(color = Color(0xFF55524E), radius = lensRadius, center = lensCenter)
  scope.drawCircle(color = Color(0xFF111010), radius = lensRadius * 0.82f, center = lensCenter)
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFCCCCCC), Color(0xFF1F2022)),
      center = lensCenter,
      radius = lensRadius * 0.55f
    ),
    radius = lensRadius * 0.55f,
    center = lensCenter
  )
}

// 10. Cinema Camera
private fun drawCinemaCamera(scope: DrawScope) {
  val w = scope.size.width
  val h = scope.size.height
  val bodyTop = h * 0.28f
  val bodyH = h * 0.58f
  val bodyW = w * 0.88f
  val bodyX = (w - bodyW) / 2

  // Top carry handle
  scope.drawRoundRect(
    color = Color(0xFF3D3A37),
    topLeft = Offset(w * 0.32f, bodyTop - h * 0.08f),
    size = Size(w * 0.36f, h * 0.06f),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // Modular cinema body
  scope.drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF2B363A), Color(0xFF161F22))
    ),
    topLeft = Offset(bodyX, bodyTop),
    size = Size(bodyW, bodyH),
    cornerRadius = CornerRadius(12f, 12f)
  )

  // Large cinema prime lens with blue coating
  val lensCenter = Offset(w * 0.5f, bodyTop + bodyH * 0.52f)
  val lensRadius = bodyH * 0.35f
  scope.drawCircle(color = Color(0xFFE5A856), radius = lensRadius + 2f, center = lensCenter, style = Stroke(2f))
  scope.drawCircle(color = Color(0xFF11171A), radius = lensRadius, center = lensCenter)
  scope.drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF3BB0C9), Color(0xFFE5A856), Color(0xFF0C1417)),
      center = lensCenter,
      radius = lensRadius * 0.65f
    ),
    radius = lensRadius * 0.65f,
    center = lensCenter
  )
}
