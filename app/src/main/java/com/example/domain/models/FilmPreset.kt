package com.example.domain.models

data class FilmPreset(
  val id: String,
  val name: String,
  val category: String, // "All", "Film", "Digital", "Instant", "Disposable", "Video", "Creative", "B&W"
  val subtitle: String,
  val description: String,
  val cameraType: CameraIllustrationType,
  val badgeColor: Long = 0xFFE5A856,
  val defaultParams: FilmProcessingParams,
  val isFavorite: Boolean = false
)

enum class CameraIllustrationType {
  RANGEFINDER_35MM,
  KODAK_GOLD,
  FUJI_COMPACT,
  PORTRA_CHROME,
  DISPOSABLE_YELLOW,
  CCD_DIGITAL,
  VHS_CAMCORDER,
  POLAROID_INSTANT,
  MONO_SLR,
  CINEMA_CAMERA
}

object FilmPresetRepository {
  val allPresets = listOf(
    FilmPreset(
      id = "classic_35mm",
      name = "35mm Classic",
      category = "Film",
      subtitle = "Warm • Film Grain",
      description = "Timeless 35mm negative film with natural contrast and organic grain.",
      cameraType = CameraIllustrationType.RANGEFINDER_35MM,
      badgeColor = 0xFF4A453F,
      defaultParams = FilmProcessingParams(
        exposure = 0.05f,
        contrast = 1.1f,
        temperature = 0.2f,
        tint = 0.05f,
        saturation = 1.15f,
        grain = 0.4f,
        vignette = 0.35f,
        halation = 0.2f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "kodak_gold",
      name = "Kodak Gold",
      category = "Film",
      subtitle = "Vibrant • Nostalgic",
      description = "Soft golden hour tones, rich amber highlights and nostalgic warmth.",
      cameraType = CameraIllustrationType.KODAK_GOLD,
      badgeColor = 0xFFE5A856,
      defaultParams = FilmProcessingParams(
        exposure = 0.1f,
        contrast = 1.15f,
        temperature = 0.45f,
        tint = 0.1f,
        saturation = 1.25f,
        grain = 0.35f,
        vignette = 0.3f,
        lightLeak = 0.2f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "fuji_c200",
      name = "Fuji C200",
      category = "Film",
      subtitle = "Soft • Natural",
      description = "Gentle emerald greens and soft pastel skin tones for everyday scenes.",
      cameraType = CameraIllustrationType.FUJI_COMPACT,
      badgeColor = 0xFF4E7D63,
      defaultParams = FilmProcessingParams(
        exposure = 0.05f,
        contrast = 1.05f,
        temperature = -0.1f,
        tint = -0.15f,
        saturation = 1.05f,
        grain = 0.3f,
        vignette = 0.25f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "portra_400",
      name = "Portra 400",
      category = "Film",
      subtitle = "Cinematic • Soft",
      description = "Subtle contrast, creamy highlights, and flattering cinematic latitude.",
      cameraType = CameraIllustrationType.PORTRA_CHROME,
      badgeColor = 0xFF696B70,
      defaultParams = FilmProcessingParams(
        exposure = 0.08f,
        contrast = 1.0f,
        temperature = 0.15f,
        tint = 0.05f,
        saturation = 1.05f,
        highlightRolloff = 0.5f,
        grain = 0.25f,
        bloom = 0.2f,
        vignette = 0.2f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "disposable",
      name = "Disposable",
      category = "Disposable",
      subtitle = "Raw • Grainy",
      description = "Punchy high-contrast flash look with pronounced plastic lens vignette.",
      cameraType = CameraIllustrationType.DISPOSABLE_YELLOW,
      badgeColor = 0xFFC99727,
      defaultParams = FilmProcessingParams(
        exposure = 0.15f,
        contrast = 1.35f,
        temperature = 0.25f,
        tint = 0.1f,
        saturation = 1.3f,
        grain = 0.65f,
        vignette = 0.55f,
        lightLeak = 0.35f,
        chromaticAberration = 0.25f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "ccd_digital",
      name = "CCD",
      category = "Digital",
      subtitle = "Early 2000s • Digital",
      description = "Early 2000s point-and-shoot camera character with crisp highlights.",
      cameraType = CameraIllustrationType.CCD_DIGITAL,
      badgeColor = 0xFF8595A6,
      defaultParams = FilmProcessingParams(
        exposure = 0.1f,
        contrast = 1.2f,
        temperature = -0.2f,
        tint = 0.0f,
        saturation = 1.2f,
        grain = 0.15f,
        sharpness = 0.4f,
        vignette = 0.15f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "vhs_tape",
      name = "VHS",
      category = "Video",
      subtitle = "Retro • Distorted",
      description = "Nostalgic magnetic tape aesthetic with scanline warmth and chromatic shift.",
      cameraType = CameraIllustrationType.VHS_CAMCORDER,
      badgeColor = 0xFF52445E,
      defaultParams = FilmProcessingParams(
        exposure = 0.0f,
        contrast = 1.1f,
        temperature = 0.1f,
        tint = 0.2f,
        saturation = 1.15f,
        grain = 0.5f,
        chromaticAberration = 0.45f,
        vignette = 0.35f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "polaroid_instant",
      name = "Instant",
      category = "Instant",
      subtitle = "Instant • Classic",
      description = "Creamy lifted shadows, soft mellow saturation, and instant-film magic.",
      cameraType = CameraIllustrationType.POLAROID_INSTANT,
      badgeColor = 0xFFD8D2C5,
      defaultParams = FilmProcessingParams(
        exposure = 0.12f,
        contrast = 0.95f,
        temperature = 0.2f,
        tint = -0.05f,
        saturation = 0.95f,
        shadowResponse = 0.4f,
        grain = 0.3f,
        bloom = 0.25f,
        vignette = 0.4f,
        frameStyle = "polaroid",
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "mono_silver",
      name = "Mono",
      category = "B&W",
      subtitle = "Clean • Monochrome",
      description = "Deep silver gelatin tones with velvety blacks and organic silver grain.",
      cameraType = CameraIllustrationType.MONO_SLR,
      badgeColor = 0xFF353434,
      defaultParams = FilmProcessingParams(
        exposure = 0.0f,
        contrast = 1.3f,
        temperature = 0.0f,
        tint = 0.0f,
        saturation = 0.0f,
        grain = 0.5f,
        vignette = 0.35f,
        dateStampEnabled = false
      )
    ),
    FilmPreset(
      id = "cinema_teal_amber",
      name = "Cinema",
      category = "Creative",
      subtitle = "Cinematic • Color",
      description = "Hollywood 35mm stock grading with teal shadows and warm golden skin highlights.",
      cameraType = CameraIllustrationType.CINEMA_CAMERA,
      badgeColor = 0xFF2D5760,
      defaultParams = FilmProcessingParams(
        exposure = 0.05f,
        contrast = 1.25f,
        temperature = 0.1f,
        tint = -0.1f,
        saturation = 1.1f,
        halation = 0.35f,
        vignette = 0.4f,
        dateStampEnabled = true
      )
    )
  )

  fun getPresetById(id: String): FilmPreset {
    return allPresets.find { it.id == id } ?: allPresets.first()
  }
}
