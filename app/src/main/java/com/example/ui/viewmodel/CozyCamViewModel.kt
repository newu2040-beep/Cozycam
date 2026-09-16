package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.datastore.UserPreferencesRepository
import com.example.data.media.MediaStoreManager
import com.example.data.room.CozyCamDatabase
import com.example.data.room.PhotoEntity
import com.example.domain.models.CameraSettings
import com.example.domain.models.FilmPreset
import com.example.domain.models.FilmPresetRepository
import com.example.domain.models.FilmProcessingParams
import com.example.processing.FilmProcessor
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
  WELCOME,
  CAMERA,
  PRESETS,
  EDITOR,
  GALLERY
}

class CozyCamViewModel(application: Application) : AndroidViewModel(application) {

  private val database = CozyCamDatabase.getDatabase(application)
  private val photoDao = database.photoDao()
  private val mediaStoreManager = MediaStoreManager(application)
  private val userPrefs = UserPreferencesRepository(application)
  private val vibrator = application.getSystemService(Vibrator::class.java)

  // Navigation state
  private val _currentScreen = MutableStateFlow(AppScreen.WELCOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  // Selected Film Preset
  private val _activePreset = MutableStateFlow(FilmPresetRepository.allPresets.first())
  val activePreset: StateFlow<FilmPreset> = _activePreset.asStateFlow()

  // Current Editing / Capture Parameters
  private val _currentParams = MutableStateFlow(FilmPresetRepository.allPresets.first().defaultParams)
  val currentParams: StateFlow<FilmProcessingParams> = _currentParams.asStateFlow()

  // Camera Settings
  private val _cameraSettings = MutableStateFlow(CameraSettings())
  val cameraSettings: StateFlow<CameraSettings> = _cameraSettings.asStateFlow()

  // Photo Editor Current Image
  private val _currentCapturedBitmap = MutableStateFlow<Bitmap?>(null)
  val currentCapturedBitmap: StateFlow<Bitmap?> = _currentCapturedBitmap.asStateFlow()

  private val _currentProcessedBitmap = MutableStateFlow<Bitmap?>(null)
  val currentProcessedBitmap: StateFlow<Bitmap?> = _currentProcessedBitmap.asStateFlow()

  private val _currentPhotoEntity = MutableStateFlow<PhotoEntity?>(null)
  val currentPhotoEntity: StateFlow<PhotoEntity?> = _currentPhotoEntity.asStateFlow()

  // Timer countdown state
  private val _timerCountdown = MutableStateFlow<Int?>(null)
  val timerCountdown: StateFlow<Int?> = _timerCountdown.asStateFlow()

  // Shutter flash animation trigger
  private val _shutterFlashAnim = MutableStateFlow(false)
  val shutterFlashAnim: StateFlow<Boolean> = _shutterFlashAnim.asStateFlow()

  // Processing state
  private val _isProcessing = MutableStateFlow(false)
  val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

  // Save status banner
  private val _saveMessage = MutableStateFlow<String?>(null)
  val saveMessage: StateFlow<String?> = _saveMessage.asStateFlow()

  // Room DB photos
  val photos: StateFlow<List<PhotoEntity>> = photoDao.getAllPhotos().stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Presets list with favorite tracking
  private val _favoritePresetIds = MutableStateFlow<Set<String>>(emptySet())
  val favoritePresetIds: StateFlow<Set<String>> = _favoritePresetIds.asStateFlow()

  init {
    viewModelScope.launch {
      userPrefs.isFirstLaunch.collect { isFirst ->
        if (!isFirst && _currentScreen.value == AppScreen.WELCOME) {
          _currentScreen.value = AppScreen.CAMERA
        }
      }
    }
  }

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun completeWelcome() {
    viewModelScope.launch {
      userPrefs.setFirstLaunchCompleted()
      _currentScreen.value = AppScreen.CAMERA
    }
  }

  fun selectPreset(preset: FilmPreset) {
    _activePreset.value = preset
    _currentParams.value = preset.defaultParams
    performHapticFeedback()
  }

  fun togglePresetFavorite(presetId: String) {
    val current = _favoritePresetIds.value
    _favoritePresetIds.value = if (current.contains(presetId)) {
      current - presetId
    } else {
      current + presetId
    }
    performHapticFeedback()
  }

  fun updateParams(newParams: FilmProcessingParams) {
    _currentParams.value = newParams
    val base = _currentCapturedBitmap.value
    if (base != null) {
      reprocessImage(base, newParams)
    }
  }

  fun cycleAspectRatio() {
    val options = listOf("3:4", "1:1", "16:9", "9:16")
    val next = options[(options.indexOf(_cameraSettings.value.aspectRatio) + 1) % options.size]
    _cameraSettings.value = _cameraSettings.value.copy(aspectRatio = next)
    performHapticFeedback()
  }

  fun cycleResolution() {
    val options = listOf("12MP", "24MP", "8MP")
    val next = options[(options.indexOf(_cameraSettings.value.resolution) + 1) % options.size]
    _cameraSettings.value = _cameraSettings.value.copy(resolution = next)
    performHapticFeedback()
  }

  fun cycleFlashMode() {
    val options = listOf("off", "auto", "on", "torch")
    val next = options[(options.indexOf(_cameraSettings.value.flashMode) + 1) % options.size]
    _cameraSettings.value = _cameraSettings.value.copy(flashMode = next)
    performHapticFeedback()
  }

  fun setZoom(zoom: Float) {
    _cameraSettings.value = _cameraSettings.value.copy(zoomLevel = zoom)
    performHapticFeedback()
  }

  fun toggleCameraFacing() {
    _cameraSettings.value = _cameraSettings.value.copy(
      isFrontCamera = !_cameraSettings.value.isFrontCamera
    )
    performHapticFeedback()
  }

  fun toggleVideoMode() {
    _cameraSettings.value = _cameraSettings.value.copy(
      isVideoMode = !_cameraSettings.value.isVideoMode
    )
    performHapticFeedback()
  }

  fun toggleGrid() {
    val newGrid = !_cameraSettings.value.gridEnabled
    setGridEnabled(newGrid)
  }

  fun setGridEnabled(enabled: Boolean) {
    _cameraSettings.value = _cameraSettings.value.copy(gridEnabled = enabled)
    viewModelScope.launch { userPrefs.setGridEnabled(enabled) }
    performHapticFeedback()
  }

  fun toggleHaptics() {
    val next = !_cameraSettings.value.hapticsEnabled
    setHapticsEnabled(next)
  }

  fun setHapticsEnabled(enabled: Boolean) {
    _cameraSettings.value = _cameraSettings.value.copy(hapticsEnabled = enabled)
    viewModelScope.launch { userPrefs.setHapticsEnabled(enabled) }
    if (enabled) performHapticFeedback()
  }

  fun toggleDateStamp() {
    val next = !_cameraSettings.value.dateStampEnabled
    setDateStampEnabled(next)
  }

  fun setDateStampEnabled(enabled: Boolean) {
    _cameraSettings.value = _cameraSettings.value.copy(dateStampEnabled = enabled)
    _currentParams.value = _currentParams.value.copy(dateStampEnabled = enabled)
    viewModelScope.launch { userPrefs.setDateStampEnabled(enabled) }
    performHapticFeedback()
  }

  fun setTimerSeconds(seconds: Int) {
    _cameraSettings.value = _cameraSettings.value.copy(timerSeconds = seconds)
    viewModelScope.launch { userPrefs.setTimerSeconds(seconds) }
    performHapticFeedback()
  }

  fun setResolution(resolution: String) {
    _cameraSettings.value = _cameraSettings.value.copy(resolution = resolution)
    viewModelScope.launch { userPrefs.setExportQuality(resolution) }
    performHapticFeedback()
  }

  fun setFlashMode(mode: String) {
    _cameraSettings.value = _cameraSettings.value.copy(flashMode = mode)
    viewModelScope.launch { userPrefs.setFlashMode(mode) }
    performHapticFeedback()
  }

  fun setAspectRatio(ratio: String) {
    _cameraSettings.value = _cameraSettings.value.copy(aspectRatio = ratio)
    viewModelScope.launch { userPrefs.setAspectRatio(ratio) }
    performHapticFeedback()
  }

  // Active playing video entity (for video player modal)
  private val _activePlayingVideo = MutableStateFlow<PhotoEntity?>(null)
  val activePlayingVideo: StateFlow<PhotoEntity?> = _activePlayingVideo.asStateFlow()

  fun playVideo(photo: PhotoEntity) {
    _activePlayingVideo.value = photo
  }

  fun closeVideoPlayer() {
    _activePlayingVideo.value = null
  }

  fun setLiveExposure(ev: Float) {
    _cameraSettings.value = _cameraSettings.value.copy(exposureCompensation = ev)
  }

  fun setLiveTemperature(temp: Float) {
    _cameraSettings.value = _cameraSettings.value.copy(liveTemperature = temp)
  }

  fun setVideoQuality(quality: String) {
    _cameraSettings.value = _cameraSettings.value.copy(videoQuality = quality)
  }

  fun setVideoFps(fps: Int) {
    _cameraSettings.value = _cameraSettings.value.copy(videoFps = fps)
  }

  fun setVideoAudioEnabled(enabled: Boolean) {
    _cameraSettings.value = _cameraSettings.value.copy(videoAudioEnabled = enabled)
  }

  fun setSampleScene(scene: String) {
    _cameraSettings.value = _cameraSettings.value.copy(activeSampleScene = scene)
  }

  fun onPhotoImported(uri: Uri) {
    viewModelScope.launch {
      _isProcessing.value = true
      val context = getApplication<Application>()
      val bmp = withContext(Dispatchers.IO) {
        try {
          context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
          }
        } catch (e: Exception) {
          null
        }
      } ?: loadSampleBitmap()

      _currentCapturedBitmap.value = bmp
      val baseParams = _currentParams.value
      val combinedParams = baseParams.copy(
        exposure = (baseParams.exposure + _cameraSettings.value.exposureCompensation).coerceIn(-1.0f, 1.0f),
        temperature = (baseParams.temperature + _cameraSettings.value.liveTemperature).coerceIn(-1.0f, 1.0f)
      )
      val processed = withContext(Dispatchers.Default) {
        FilmProcessor.processBitmap(bmp, combinedParams)
      }
      _currentProcessedBitmap.value = processed

      // Automatically save to MediaStore Gallery (Pictures/CozyCam)
      val filename = "COZY_IMPORT_${System.currentTimeMillis()}.jpg"
      mediaStoreManager.saveBitmapToMediaStore(processed, filename)
      val localFile = mediaStoreManager.saveLocally(processed, filename)

      val entity = PhotoEntity(
        uri = localFile.absolutePath,
        presetId = _activePreset.value.id,
        presetName = _activePreset.value.name,
        width = processed.width,
        height = processed.height,
        camera = "imported"
      )
      val newId = photoDao.insertPhoto(entity)
      _currentPhotoEntity.value = entity.copy(id = newId)

      _isProcessing.value = false
      _saveMessage.value = "Photo Imported & Filter Applied!"
      _currentScreen.value = AppScreen.EDITOR
    }
  }

  fun onShutterTriggered(cameraBitmap: Bitmap? = null) {
    viewModelScope.launch {
      // Trigger shutter flash
      _shutterFlashAnim.value = true
      performHapticFeedback()
      delay(120)
      _shutterFlashAnim.value = false

      _isProcessing.value = true

      // If cameraBitmap is provided and valid, use it; otherwise generate rich vintage scene frame
      val isBlank = cameraBitmap == null || isBitmapBlank(cameraBitmap)
      val rawBitmap = if (!isBlank && cameraBitmap != null) {
        cameraBitmap
      } else {
        loadSampleBitmap()
      }
      _currentCapturedBitmap.value = rawBitmap

      // Apply film processing profile combined with live exposure and live temperature
      val currentP = _currentParams.value
      val combinedParams = currentP.copy(
        exposure = (currentP.exposure + _cameraSettings.value.exposureCompensation).coerceIn(-1.0f, 1.0f),
        temperature = (currentP.temperature + _cameraSettings.value.liveTemperature).coerceIn(-1.0f, 1.0f)
      )
      val processed = withContext(Dispatchers.Default) {
        FilmProcessor.processBitmap(rawBitmap, combinedParams)
      }
      _currentProcessedBitmap.value = processed

      // Automatically save to MediaStore Gallery (Pictures/CozyCam) and local storage
      val filename = "COZY_${System.currentTimeMillis()}.jpg"
      val savedUri = mediaStoreManager.saveBitmapToMediaStore(processed, filename)
      val localFile = mediaStoreManager.saveLocally(processed, filename)

      val entity = PhotoEntity(
        uri = localFile.absolutePath,
        presetId = _activePreset.value.id,
        presetName = _activePreset.value.name,
        width = processed.width,
        height = processed.height,
        camera = if (_cameraSettings.value.isFrontCamera) "front" else "rear"
      )
      val newId = photoDao.insertPhoto(entity)
      _currentPhotoEntity.value = entity.copy(id = newId)

      NotificationHelper.showMediaSavedNotification(
        getApplication(),
        "Photo Auto-Saved",
        "Your vintage photo was automatically saved to Pictures/CozyCam"
      )
      _saveMessage.value = "Photo Saved to Gallery"

      _isProcessing.value = false
      // Stay on CAMERA screen so user can shoot continuous photos, exactly like native camera
    }
  }

  fun onVideoRecorded(videoFile: File) {
    viewModelScope.launch {
      _isProcessing.value = true
      val filename = "COZY_${System.currentTimeMillis()}.mp4"
      val savedUri = mediaStoreManager.saveVideoToMediaStore(videoFile, filename)

      val entity = PhotoEntity(
        uri = videoFile.absolutePath,
        presetId = _activePreset.value.id,
        presetName = "${_activePreset.value.name} (Video)",
        width = 1080,
        height = 1920,
        camera = if (_cameraSettings.value.isFrontCamera) "front" else "rear"
      )
      photoDao.insertPhoto(entity)

      NotificationHelper.showMediaSavedNotification(
        getApplication(),
        "Video Auto-Saved",
        "Your vintage video was automatically saved to Movies/CozyCam"
      )

      _isProcessing.value = false
      _saveMessage.value = "Video auto-saved to Gallery"
      performHapticFeedback()
      delay(3000)
      _saveMessage.value = null
    }
  }

  fun openPhotoInEditor(photo: PhotoEntity) {
    viewModelScope.launch {
      if (photo.uri.endsWith(".mp4") || photo.presetName.contains("Video")) {
        playVideo(photo)
        return@launch
      }

      _currentPhotoEntity.value = photo
      val preset = FilmPresetRepository.getPresetById(photo.presetId)
      _activePreset.value = preset
      _currentParams.value = preset.defaultParams

      val file = File(photo.uri)
      val bitmap = if (file.exists() && !file.name.endsWith(".mp4")) {
        BitmapFactory.decodeFile(file.absolutePath) ?: loadSampleBitmap()
      } else {
        loadSampleBitmap()
      }
      _currentCapturedBitmap.value = bitmap
      _currentProcessedBitmap.value = bitmap
      _currentScreen.value = AppScreen.EDITOR
    }
  }

  private fun isBitmapBlank(bitmap: Bitmap): Boolean {
    val w = bitmap.width
    val h = bitmap.height
    if (w <= 0 || h <= 0) return true
    var nonBlackPixels = 0
    val stepX = (w / 12).coerceAtLeast(1)
    val stepY = (h / 12).coerceAtLeast(1)
    var totalSampled = 0
    for (x in stepX until w step stepX) {
      for (y in stepY until h step stepY) {
        val pixel = bitmap.getPixel(x, y)
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        totalSampled++
        if (r > 15 || g > 15 || b > 15) {
          nonBlackPixels++
        }
      }
    }
    return totalSampled > 0 && (nonBlackPixels.toFloat() / totalSampled) < 0.02f
  }

  private fun loadSampleBitmap(): Bitmap {
    return FilmProcessor.createVintageSceneBitmap(_cameraSettings.value.activeSampleScene, 1080, 1440)
  }

  private fun reprocessImage(base: Bitmap, params: FilmProcessingParams) {
    viewModelScope.launch {
      val processed = withContext(Dispatchers.Default) {
        FilmProcessor.processBitmap(base, params)
      }
      _currentProcessedBitmap.value = processed
    }
  }

  fun saveCurrentPhotoToMediaStore() {
    val bitmap = _currentProcessedBitmap.value ?: return
    viewModelScope.launch {
      _isProcessing.value = true
      val uri = mediaStoreManager.saveBitmapToMediaStore(bitmap)
      _isProcessing.value = false
      if (uri != null) {
        _saveMessage.value = "Saved to CozyCam Album"
      } else {
        _saveMessage.value = "Saved to Gallery"
      }
      performHapticFeedback()
      delay(2500)
      _saveMessage.value = null
    }
  }

  fun shareCurrentPhoto() {
    val bitmap = _currentProcessedBitmap.value ?: return
    mediaStoreManager.shareImage(bitmap, "Captured with CozyCam • ${_activePreset.value.name}")
    performHapticFeedback()
  }

  fun toggleCurrentFavorite() {
    val current = _currentPhotoEntity.value ?: return
    viewModelScope.launch {
      val newFav = !current.isFavorite
      photoDao.updateFavorite(current.id, newFav)
      _currentPhotoEntity.value = current.copy(isFavorite = newFav)
      performHapticFeedback()
    }
  }

  fun deleteCurrentPhoto() {
    val current = _currentPhotoEntity.value ?: return
    viewModelScope.launch {
      photoDao.deletePhotoById(current.id)
      _currentPhotoEntity.value = null
      _currentProcessedBitmap.value = null
      _currentCapturedBitmap.value = null
      _currentScreen.value = AppScreen.CAMERA
      performHapticFeedback()
    }
  }

  fun togglePhotoFavorite(photoId: Long, currentFav: Boolean) {
    viewModelScope.launch {
      photoDao.updateFavorite(photoId, !currentFav)
      performHapticFeedback()
    }
  }

  fun deletePhotoById(photoId: Long) {
    viewModelScope.launch {
      photoDao.deletePhotoById(photoId)
      performHapticFeedback()
    }
  }

  fun performHapticFeedback() {
    try {
      if (_cameraSettings.value.hapticsEnabled && vibrator != null && vibrator.hasVibrator()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(25)
        }
      }
    } catch (e: Exception) {
      // Ignore vibration errors
    }
  }
}
