package com.example.processing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.example.domain.models.FilmProcessingParams
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import kotlin.math.max
import kotlin.math.min

object FilmProcessor {

  /**
   * Applies the complete vintage film processing pipeline to a source Bitmap.
   */
  fun processBitmap(source: Bitmap, params: FilmProcessingParams): Bitmap {
    val width = source.width
    val height = source.height
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    // 1. Base color grading with ColorMatrix
    val colorMatrix = createFilmColorMatrix(params)
    val basePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
      colorFilter = ColorMatrixColorFilter(colorMatrix)
    }
    canvas.drawBitmap(source, 0f, 0f, basePaint)

    // 2. Halation / Bloom (soft warm glow on highlights)
    if (params.bloom > 0.05f || params.halation > 0.05f) {
      applyHalationBloom(canvas, width, height, params.bloom, params.halation)
    }

    // 3. Light Leak
    if (params.lightLeak > 0.05f) {
      applyLightLeak(canvas, width, height, params.lightLeak)
    }

    // 4. Procedural Film Grain
    if (params.grain > 0.05f) {
      applyProceduralGrain(canvas, width, height, params.grain)
    }

    // 5. Analog Dust & Scratches
    if (params.dust > 0.05f) {
      applyDustAndScratches(canvas, width, height, params.dust)
    }

    // 6. Vignette Shading
    if (params.vignette > 0.05f) {
      applyVignette(canvas, width, height, params.vignette)
    }

    // 7. Date Stamp (Authentic 80s/90s amber LED font)
    if (params.dateStampEnabled) {
      drawDateStamp(canvas, width, height, params.dateStampText)
    }

    // 8. Optional Film Framing
    if (params.frameStyle != "none") {
      return applyFrame(output, params.frameStyle)
    }

    return output
  }

  private fun createFilmColorMatrix(params: FilmProcessingParams): ColorMatrix {
    val cm = ColorMatrix()

    // Saturation
    cm.setSaturation(params.saturation)

    // Exposure adjustment
    val exposureShift = (params.exposure * 60f)

    // Contrast adjustment
    val contrast = params.contrast
    val contrastShift = (1f - contrast) * 128f

    // Temperature & Tint adjustment
    // Warm temperature increases Red, slightly decreases Blue
    val tempRed = 1f + (params.temperature * 0.25f)
    val tempBlue = 1f - (params.temperature * 0.25f)
    val tintGreen = 1f - (params.tint * 0.2f)
    val tintRed = 1f + (params.tint * 0.1f)

    // Shadow lifting (matte blacks)
    val shadowLift = params.shadowResponse * 24f

    val matrixArray = floatArrayOf(
      contrast * tempRed * tintRed, 0f, 0f, 0f, exposureShift + contrastShift + shadowLift,
      0f, contrast * tintGreen, 0f, 0f, exposureShift + contrastShift + shadowLift * 0.8f,
      0f, 0f, contrast * tempBlue, 0f, exposureShift + contrastShift + shadowLift * 0.6f,
      0f, 0f, 0f, 1f, 0f
    )

    val gradeMatrix = ColorMatrix(matrixArray)
    cm.postConcat(gradeMatrix)
    return cm
  }

  private fun applyHalationBloom(canvas: Canvas, w: Int, h: Int, bloom: Float, halation: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
    }

    val glowColor = if (halation > bloom) {
      // Warm amber/red halation
      Color.argb((min(1f, halation * 0.35f) * 255).toInt(), 255, 120, 50)
    } else {
      // Creamy soft bloom
      Color.argb((min(1f, bloom * 0.3f) * 255).toInt(), 255, 240, 210)
    }

    val radius = max(w, h) * 0.8f
    val shader = RadialGradient(
      w * 0.5f, h * 0.35f, radius,
      intArrayOf(glowColor, Color.TRANSPARENT),
      floatArrayOf(0.1f, 1.0f),
      Shader.TileMode.CLAMP
    )
    paint.shader = shader
    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
  }

  private fun applyLightLeak(canvas: Canvas, w: Int, h: Int, intensity: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
    }
    val alpha = (min(1f, intensity * 0.7f) * 255).toInt()
    val color1 = Color.argb(alpha, 255, 140, 40)
    val color2 = Color.argb((alpha * 0.5f).toInt(), 255, 70, 20)
    val color3 = Color.TRANSPARENT

    // Diagonal light flare from top right / bottom edge
    val shader = LinearGradient(
      w.toFloat(), 0f, w * 0.3f, h * 0.7f,
      intArrayOf(color1, color2, color3),
      floatArrayOf(0.0f, 0.4f, 1.0f),
      Shader.TileMode.CLAMP
    )
    paint.shader = shader
    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
  }

  private fun applyProceduralGrain(canvas: Canvas, w: Int, h: Int, grainIntensity: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val random = Random(42)
    val density = (grainIntensity * 12000).toInt()
    val maxAlpha = min(120, (grainIntensity * 90).toInt())

    for (i in 0 until density) {
      val x = random.nextFloat() * w
      val y = random.nextFloat() * h
      val isDark = random.nextBoolean()
      val alpha = random.nextInt(max(1, maxAlpha))
      paint.color = if (isDark) Color.argb(alpha, 10, 10, 10) else Color.argb(alpha, 240, 240, 240)
      paint.strokeWidth = random.nextFloat() * 2f + 0.8f
      canvas.drawPoint(x, y, paint)
    }
  }

  private fun applyDustAndScratches(canvas: Canvas, w: Int, h: Int, dustIntensity: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      strokeCap = Paint.Cap.ROUND
    }
    val random = Random(101)
    val count = (dustIntensity * 35).toInt()

    for (i in 0 until count) {
      val x = random.nextFloat() * w
      val y = random.nextFloat() * h
      val alpha = random.nextInt(80) + 40
      paint.color = Color.argb(alpha, 245, 240, 230)

      if (random.nextFloat() > 0.4f) {
        // Small speck
        val r = random.nextFloat() * 2.5f + 1f
        canvas.drawCircle(x, y, r, paint)
      } else {
        // Hair/scratch
        paint.strokeWidth = 1.2f
        val len = random.nextFloat() * 18f + 6f
        val angle = random.nextFloat() * 3.14f
        val x2 = x + (Math.cos(angle.toDouble()) * len).toFloat()
        val y2 = y + (Math.sin(angle.toDouble()) * len).toFloat()
        canvas.drawLine(x, y, x2, y2, paint)
      }
    }
  }

  private fun applyVignette(canvas: Canvas, w: Int, h: Int, intensity: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }
    val alpha = (min(1f, intensity * 0.95f) * 255).toInt()
    val vignetteColor = Color.argb(alpha, 20, 15, 12)

    val radius = max(w, h) * 0.72f
    val shader = RadialGradient(
      w * 0.5f, h * 0.5f, radius,
      intArrayOf(Color.TRANSPARENT, vignetteColor),
      floatArrayOf(0.45f, 1.0f),
      Shader.TileMode.CLAMP
    )
    paint.shader = shader
    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
  }

  fun drawDateStamp(canvas: Canvas, w: Int, h: Int, customText: String = "") {
    val text = if (customText.isNotBlank()) {
      customText
    } else {
      val sdf = SimpleDateFormat("MMM d yyyy", Locale.US)
      sdf.format(Date()).uppercase()
    }

    val fontSize = max(18f, min(w, h) * 0.038f)
    val marginX = w * 0.06f
    val marginY = h * 0.94f

    // Retro Glow Paint
    val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.argb(120, 255, 120, 20)
      textSize = fontSize
      typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      letterSpacing = 0.22f
    }
    canvas.drawText(text, marginX + 1f, marginY + 1f, glowPaint)

    // Retro Amber Core Paint
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.rgb(255, 154, 34) // Classic 90s quartz date amber
      textSize = fontSize
      typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      letterSpacing = 0.22f
    }
    canvas.drawText(text, marginX, marginY, textPaint)
  }

  private fun applyFrame(source: Bitmap, frameStyle: String): Bitmap {
    val sw = source.width
    val sh = source.height

    if (frameStyle == "polaroid") {
      val borderSide = (sw * 0.08f).toInt()
      val borderTop = (sh * 0.08f).toInt()
      val borderBottom = (sh * 0.24f).toInt()

      val targetW = sw + (borderSide * 2)
      val targetH = sh + borderTop + borderBottom
      val framed = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(framed)

      // Cream white card
      val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(246, 243, 235)
      }
      canvas.drawRect(0f, 0f, targetW.toFloat(), targetH.toFloat(), cardPaint)

      // Inner subtle border
      val innerBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(30, 0, 0, 0)
        style = Paint.Style.STROKE
        strokeWidth = 2f
      }
      canvas.drawRect(
        borderSide.toFloat(), borderTop.toFloat(),
        (borderSide + sw).toFloat(), (borderTop + sh).toFloat(),
        innerBorderPaint
      )

      canvas.drawBitmap(source, borderSide.toFloat(), borderTop.toFloat(), null)
      return framed
    } else if (frameStyle == "film35mm") {
      val border = (sw * 0.07f).toInt()
      val targetW = sw + (border * 2)
      val framed = Bitmap.createBitmap(targetW, sh, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(framed)

      val filmBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(18, 17, 16)
      }
      canvas.drawRect(0f, 0f, targetW.toFloat(), sh.toFloat(), filmBorderPaint)
      canvas.drawBitmap(source, border.toFloat(), 0f, null)

      // Sprocket holes simulation
      val sprocketPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(38, 36, 34)
      }
      val sprocketH = (sh * 0.035f)
      val sprocketW = (border * 0.5f)
      val gap = sprocketH * 1.8f
      var y = gap
      while (y < sh - gap) {
        val rectLeft = RectF((border - sprocketW) * 0.5f, y, (border + sprocketW) * 0.5f, y + sprocketH)
        val rectRight = RectF(targetW - (border + sprocketW) * 0.5f, y, targetW - (border - sprocketW) * 0.5f, y + sprocketH)
        canvas.drawRoundRect(rectLeft, 4f, 4f, sprocketPaint)
        canvas.drawRoundRect(rectRight, 4f, 4f, sprocketPaint)
        y += gap
      }
      return framed
    }

    return source
  }

  fun createSampleBitmap(width: Int = 1080, height: Int = 1440): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Warm golden hour sunset background gradient
    val skyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      shader = LinearGradient(
        0f, 0f, 0f, height.toFloat(),
        intArrayOf(
          Color.rgb(45, 35, 60),   // Twilight indigo
          Color.rgb(175, 80, 50),  // Deep burnt orange
          Color.rgb(240, 160, 60), // Golden amber
          Color.rgb(255, 215, 120),// Soft yellow
          Color.rgb(25, 20, 25)    // Ground silhouette
        ),
        floatArrayOf(0f, 0.35f, 0.65f, 0.8f, 1f),
        Shader.TileMode.CLAMP
      )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), skyPaint)

    // City skyline & street silhouette
    val cityPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.rgb(22, 18, 22)
    }

    // Street perspective
    val groundY = height * 0.75f
    canvas.drawRect(0f, groundY, width.toFloat(), height.toFloat(), cityPaint)

    // Buildings silhouette
    val b1 = RectF(0f, groundY - 260f, width * 0.32f, groundY)
    val b2 = RectF(width * 0.68f, groundY - 320f, width.toFloat(), groundY)
    val b3 = RectF(width * 0.28f, groundY - 140f, width * 0.45f, groundY)
    val b4 = RectF(width * 0.55f, groundY - 180f, width * 0.72f, groundY)
    canvas.drawRect(b1, cityPaint)
    canvas.drawRect(b2, cityPaint)
    canvas.drawRect(b3, cityPaint)
    canvas.drawRect(b4, cityPaint)

    // Glowing street lights & car taillights
    val lightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.rgb(255, 220, 140)
    }
    val tailLightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.rgb(255, 80, 50)
    }

    val carY = groundY + 120f
    canvas.drawCircle(width * 0.48f, carY, 6f, tailLightPaint)
    canvas.drawCircle(width * 0.52f, carY, 6f, tailLightPaint)
    canvas.drawCircle(width * 0.42f, carY + 80f, 8f, tailLightPaint)
    canvas.drawCircle(width * 0.47f, carY + 80f, 8f, tailLightPaint)

    // Street lamp posts
    canvas.drawCircle(width * 0.25f, groundY - 30f, 12f, lightPaint)
    canvas.drawCircle(width * 0.75f, groundY - 50f, 12f, lightPaint)

    return bitmap
  }
}
