package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.AppScreen
import com.example.ui.screens.CameraScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.PresetsScreen
import com.example.ui.screens.SettingsSheet
import com.example.ui.screens.WelcomeScreen
import com.example.ui.viewmodel.CozyCamViewModel

@Composable
fun CozyCamApp(
  viewModel: CozyCamViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val activePreset by viewModel.activePreset.collectAsState()
  val currentParams by viewModel.currentParams.collectAsState()
  val cameraSettings by viewModel.cameraSettings.collectAsState()
  val processedBitmap by viewModel.currentProcessedBitmap.collectAsState()
  val photos by viewModel.photos.collectAsState()
  val favoritePresetIds by viewModel.favoritePresetIds.collectAsState()
  val timerCountdown by viewModel.timerCountdown.collectAsState()
  val shutterFlashAnim by viewModel.shutterFlashAnim.collectAsState()
  val isProcessing by viewModel.isProcessing.collectAsState()
  val saveMessage by viewModel.saveMessage.collectAsState()

  var showSettingsSheet by remember { mutableStateOf(false) }

  // System back button handling
  BackHandler(enabled = currentScreen != AppScreen.CAMERA && currentScreen != AppScreen.WELCOME) {
    viewModel.navigateTo(AppScreen.CAMERA)
  }

  AnimatedContent(
    targetState = currentScreen,
    transitionSpec = {
      if (targetState == AppScreen.EDITOR || targetState == AppScreen.PRESETS || targetState == AppScreen.GALLERY) {
        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
      } else {
        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
      }
    },
    label = "screen_transition",
    modifier = modifier.fillMaxSize()
  ) { screen ->
    when (screen) {
      AppScreen.WELCOME -> {
        WelcomeScreen(
          onGetStarted = { viewModel.completeWelcome() }
        )
      }

      AppScreen.CAMERA -> {
        CameraScreen(
          activePreset = activePreset,
          params = currentParams,
          settings = cameraSettings,
          latestPhoto = photos.firstOrNull(),
          timerCountdown = timerCountdown,
          shutterFlashAnim = shutterFlashAnim,
          onCycleAspectRatio = { viewModel.cycleAspectRatio() },
          onCycleResolution = { viewModel.cycleResolution() },
          onCycleFlash = { viewModel.cycleFlashMode() },
          onCycleTimer = {
            val next = when (cameraSettings.timerSeconds) {
              0 -> 3
              3 -> 10
              else -> 0
            }
            viewModel.setTimerSeconds(next)
          },
          onToggleGrid = { viewModel.toggleGrid() },
          onOpenSettings = { showSettingsSheet = true },
          onSetZoom = { viewModel.setZoom(it) },
          onToggleCameraFacing = { viewModel.toggleCameraFacing() },
          onToggleVideoMode = { viewModel.toggleVideoMode() },
          onOpenPresets = { viewModel.navigateTo(AppScreen.PRESETS) },
          onOpenGallery = { viewModel.navigateTo(AppScreen.GALLERY) },
          onShutterClick = { bmp -> viewModel.onShutterTriggered(bmp) },
          onVideoRecorded = { file -> viewModel.onVideoRecorded(file) }
        )

        if (showSettingsSheet) {
          SettingsSheet(
            settings = cameraSettings,
            onDismiss = { showSettingsSheet = false },
            onToggleGrid = { viewModel.toggleGrid() },
            onSetTimer = { viewModel.setTimerSeconds(it) },
            onToggleHaptics = { viewModel.toggleHaptics() }
          )
        }
      }

      AppScreen.PRESETS -> {
        PresetsScreen(
          activePreset = activePreset,
          favoritePresetIds = favoritePresetIds,
          onSelectPreset = { preset ->
            viewModel.selectPreset(preset)
            viewModel.navigateTo(AppScreen.CAMERA)
          },
          onToggleFavorite = { presetId -> viewModel.togglePresetFavorite(presetId) },
          onBack = { viewModel.navigateTo(AppScreen.CAMERA) }
        )
      }

      AppScreen.EDITOR -> {
        EditorScreen(
          processedBitmap = processedBitmap,
          params = currentParams,
          saveMessage = saveMessage,
          isProcessing = isProcessing,
          onUpdateParams = { viewModel.updateParams(it) },
          onSave = { viewModel.saveCurrentPhotoToMediaStore() },
          onShare = { viewModel.shareCurrentPhoto() },
          onDelete = { viewModel.deleteCurrentPhoto() },
          onBack = { viewModel.navigateTo(AppScreen.CAMERA) }
        )
      }

      AppScreen.GALLERY -> {
        GalleryScreen(
          photos = photos,
          onPhotoClick = { photo -> viewModel.openPhotoInEditor(photo) },
          onToggleFavorite = { id, fav -> viewModel.togglePhotoFavorite(id, fav) },
          onDeletePhoto = { id -> viewModel.deletePhotoById(id) },
          onBack = { viewModel.navigateTo(AppScreen.CAMERA) }
        )
      }
    }
  }
}
