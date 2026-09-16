package com.example.domain.models

data class FilmProcessingParams(
  val exposure: Float = 0.0f,            // -1.0 to 1.0
  val contrast: Float = 1.05f,           // 0.5 to 1.8
  val temperature: Float = 0.15f,        // -1.0 (cool) to 1.0 (warm)
  val tint: Float = 0.05f,               // -1.0 (green) to 1.0 (magenta)
  val saturation: Float = 1.1f,          // 0.0 to 2.0
  val highlightRolloff: Float = 0.3f,    // soft highlights
  val shadowResponse: Float = 0.2f,      // lifted film shadows
  val grain: Float = 0.35f,              // 0.0 to 1.0
  val halation: Float = 0.25f,           // warm glow on edges
  val bloom: Float = 0.15f,              // dreamy soft glow
  val vignette: Float = 0.3f,            // 0.0 to 1.0
  val chromaticAberration: Float = 0.1f, // 0.0 to 1.0
  val sharpness: Float = 0.1f,           // -1.0 to 1.0
  val dust: Float = 0.1f,                // vintage specks
  val lightLeak: Float = 0.0f,           // 0.0 to 1.0
  val dateStampEnabled: Boolean = true,
  val dateStampText: String = "SEP 16 2026",
  val frameStyle: String = "none"        // "none", "film35mm", "polaroid", "rounded"
)
