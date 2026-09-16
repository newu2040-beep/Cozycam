package com.example.domain.models

data class CameraSettings(
  val aspectRatio: String = "3:4",        // "3:4", "1:1", "16:9", "9:16"
  val resolution: String = "12MP",       // "8MP", "12MP", "24MP"
  val flashMode: String = "off",          // "off", "auto", "on", "torch"
  val timerSeconds: Int = 0,             // 0, 3, 10
  val gridEnabled: Boolean = true,
  val hapticsEnabled: Boolean = true,
  val shutterSoundEnabled: Boolean = true,
  val dateStampEnabled: Boolean = true,
  val saveOriginal: Boolean = false,
  val isFrontCamera: Boolean = false,
  val isVideoMode: Boolean = false,
  val zoomLevel: Float = 1.0f,
  val exposureCompensation: Float = 0.0f,
  val liveTemperature: Float = 0.0f,      // -1.0 (Cool) to 1.0 (Warm)
  val videoQuality: String = "1080p FHD", // "4K UHD", "1080p FHD", "720p HD", "480p SD"
  val videoFps: Int = 30,                 // 24, 30, 60
  val videoAudioEnabled: Boolean = true,
  val videoBitratePreset: String = "High",// "High", "Standard", "Vintage"
  val activeSampleScene: String = "sunset" // "sunset", "cafe", "coastal"
)
