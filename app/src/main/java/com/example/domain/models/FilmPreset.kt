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
    ),
    FilmPreset(
      id = "super_8mm",
      name = "Super 8",
      category = "Video",
      subtitle = "Warm • 1970s Motion",
      description = "Golden sepia glow, warm tungsten tint, organic grain and nostalgic motion-film character.",
      cameraType = CameraIllustrationType.VHS_CAMCORDER,
      badgeColor = 0xFF8A5A2B,
      defaultParams = FilmProcessingParams(
        exposure = 0.08f,
        contrast = 1.18f,
        temperature = 0.42f,
        tint = 0.15f,
        saturation = 1.2f,
        grain = 0.55f,
        vignette = 0.45f,
        halation = 0.35f,
        lightLeak = 0.25f,
        frameStyle = "film35mm",
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "cinestill_800t",
      name = "CineStill 800",
      category = "Film",
      subtitle = "Night Cine • Red Halation",
      description = "Iconic tungsten-balanced film with glowing crimson halation bloom around night lights.",
      cameraType = CameraIllustrationType.CINEMA_CAMERA,
      badgeColor = 0xFFB33927,
      defaultParams = FilmProcessingParams(
        exposure = 0.05f,
        contrast = 1.28f,
        temperature = -0.2f,
        tint = 0.12f,
        saturation = 1.15f,
        halation = 0.65f,
        bloom = 0.35f,
        grain = 0.4f,
        vignette = 0.32f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "ektachrome_100",
      name = "Ektachrome",
      category = "Film",
      subtitle = "Vivid • Slide Color",
      description = "Rich royal blues, saturated warm highlights, and punchy reversal film contrast.",
      cameraType = CameraIllustrationType.FUJI_COMPACT,
      badgeColor = 0xFF2A52BE,
      defaultParams = FilmProcessingParams(
        exposure = 0.05f,
        contrast = 1.35f,
        temperature = -0.05f,
        tint = -0.08f,
        saturation = 1.4f,
        grain = 0.22f,
        vignette = 0.25f,
        sharpness = 0.35f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "ilford_hp5",
      name = "Ilford HP5",
      category = "B&W",
      subtitle = "Street • Gritty Silver",
      description = "Classic high-contrast 400 ISO monochrome with velvety deep blacks and authentic silver gelatin grain.",
      cameraType = CameraIllustrationType.MONO_SLR,
      badgeColor = 0xFF2B2B2B,
      defaultParams = FilmProcessingParams(
        exposure = 0.0f,
        contrast = 1.42f,
        temperature = 0.0f,
        tint = 0.0f,
        saturation = 0.0f,
        grain = 0.65f,
        vignette = 0.4f,
        sharpness = 0.25f,
        dateStampEnabled = false
      )
    ),
    FilmPreset(
      id = "pastel_dream",
      name = "Pastel Dream",
      category = "Creative",
      subtitle = "Mellow • Dreamy Blush",
      description = "Milky lifted shadows, dreamy blooming highlights, and soft pastel peachy skin tones.",
      cameraType = CameraIllustrationType.POLAROID_INSTANT,
      badgeColor = 0xFFD89B9B,
      defaultParams = FilmProcessingParams(
        exposure = 0.15f,
        contrast = 0.92f,
        temperature = 0.22f,
        tint = 0.18f,
        saturation = 0.95f,
        bloom = 0.45f,
        shadowResponse = 0.45f,
        grain = 0.25f,
        vignette = 0.2f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "polaroid_600",
      name = "Polaroid 600",
      category = "Instant",
      subtitle = "High-Key • Instant Magic",
      description = "Signature 80s instant camera aesthetic with lifted shadows, cyan skies, and white instant border.",
      cameraType = CameraIllustrationType.POLAROID_INSTANT,
      badgeColor = 0xFFE2DCB8,
      defaultParams = FilmProcessingParams(
        exposure = 0.18f,
        contrast = 1.05f,
        temperature = 0.15f,
        tint = -0.1f,
        saturation = 1.1f,
        bloom = 0.35f,
        grain = 0.35f,
        vignette = 0.35f,
        frameStyle = "polaroid",
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "golden_amber",
      name = "Golden Amber",
      category = "Film",
      subtitle = "Golden Hour • Radiant",
      description = "Drenched in golden hour sunlight with radiant amber highlights, gentle light leaks, and warm honey hues.",
      cameraType = CameraIllustrationType.KODAK_GOLD,
      badgeColor = 0xFFD4881A,
      defaultParams = FilmProcessingParams(
        exposure = 0.12f,
        contrast = 1.18f,
        temperature = 0.65f,
        tint = 0.15f,
        saturation = 1.35f,
        grain = 0.3f,
        halation = 0.4f,
        lightLeak = 0.4f,
        vignette = 0.32f,
        dateStampEnabled = true
      )
    ),
    FilmPreset(
      id = "cyberpunk_neo",
      name = "Cyberpunk Neo",
      category = "Creative",
      subtitle = "Neon • Violet & Cyan",
      description = "Futuristic retro anime film grade with deep electric violet shadows and glowing neon cyan highlights.",
      cameraType = CameraIllustrationType.CINEMA_CAMERA,
      badgeColor = 0xFF8A2BE2,
      defaultParams = FilmProcessingParams(
        exposure = 0.05f,
        contrast = 1.35f,
        temperature = -0.4f,
        tint = 0.55f,
        saturation = 1.38f,
        chromaticAberration = 0.5f,
        bloom = 0.4f,
        vignette = 0.45f,
        dateStampEnabled = true
      )
    )
  )

  fun getPresetById(id: String): FilmPreset {
    return allPresets.find { it.id == id } ?: allPresets.first()
  }
}
