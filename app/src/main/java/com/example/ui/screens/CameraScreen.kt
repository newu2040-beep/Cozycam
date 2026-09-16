package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timer10
import androidx.compose.material.icons.filled.Timer3
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.camera.CameraXController
import com.example.data.room.PhotoEntity
import com.example.domain.models.CameraSettings
import com.example.domain.models.FilmPreset
import com.example.domain.models.FilmProcessingParams
import com.example.ui.components.CameraToneControlBar
import com.example.ui.components.FilmOverlayEffect
import com.example.ui.components.ShutterButton
import com.example.ui.components.VideoSettingsSheet
import com.example.ui.components.VintageCameraIllustration
import com.example.ui.theme.CozyAmberGold
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyCharcoal
import com.example.ui.theme.CozyCharcoalElevated
import com.example.ui.theme.CozyCharcoalSurface
import com.example.ui.theme.CozyCream
import com.example.ui.theme.CozyMutedText
import com.example.ui.theme.CozyObsidian
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
  activePreset: FilmPreset,
  params: FilmProcessingParams,
  settings: CameraSettings,
  latestPhoto: PhotoEntity?,
  timerCountdown: Int?,
  shutterFlashAnim: Boolean,
  onCycleAspectRatio: () -> Unit,
  onCycleResolution: () -> Unit,
  onCycleFlash: () -> Unit,
  onCycleTimer: () -> Unit,
  onToggleGrid: () -> Unit,
  onOpenSettings: () -> Unit,
  onSetZoom: (Float) -> Unit,
  onToggleCameraFacing: () -> Unit,
  onToggleVideoMode: () -> Unit,
  onOpenPresets: () -> Unit,
  onOpenGallery: () -> Unit,
  onShutterClick: (Bitmap?) -> Unit,
  onVideoRecorded: (File) -> Unit = {},
  onSetExposure: (Float) -> Unit = {},
  onSetTemperature: (Float) -> Unit = {},
  onUpdateVideoQuality: (String) -> Unit = {},
  onUpdateVideoFps: (Int) -> Unit = {},
  onToggleVideoAudio: (Boolean) -> Unit = {},
  onSelectSampleScene: (String) -> Unit = {},
  onPhotoImported: (Uri) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val allPermissions = remember {
    buildList {
      add(Manifest.permission.CAMERA)
      add(Manifest.permission.RECORD_AUDIO)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.POST_NOTIFICATIONS)
        add(Manifest.permission.READ_MEDIA_IMAGES)
        add(Manifest.permission.READ_MEDIA_VIDEO)
        add(Manifest.permission.READ_MEDIA_AUDIO)
      } else {
        add(Manifest.permission.READ_EXTERNAL_STORAGE)
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
      }
    }.toTypedArray()
  }

  var permissionsMap by remember {
    mutableStateOf(
      allPermissions.associateWith { perm ->
        ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
      }
    )
  }

  val multiplePermissionsLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { results ->
    permissionsMap = results
  }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onPhotoImported(uri)
    }
  }

  val hasCameraPermission = permissionsMap[Manifest.permission.CAMERA] == true ||
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

  LaunchedEffect(Unit) {
    val needsRequest = allPermissions.any {
      ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }
    if (needsRequest) {
      multiplePermissionsLauncher.launch(allPermissions)
    }
  }

  val cameraController = remember {
    CameraXController(context, lifecycleOwner)
  }

  val coroutineScope = rememberCoroutineScope()
  var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
  var isCameraXActive by remember { mutableStateOf(false) }
  var isRecordingVideo by remember { mutableStateOf(false) }
  var recordingDurationSeconds by remember { mutableStateOf(0) }
  var showVideoSettingsSheet by remember { mutableStateOf(false) }
  var activeTimerCountdown by remember { mutableStateOf<Int?>(null) }
  var timerJob by remember { mutableStateOf<Job?>(null) }
  var isScreenFlashActive by remember { mutableStateOf(false) }

  // Rebind camera whenever mode, facing, video quality, or flash changes
  LaunchedEffect(settings.isVideoMode, settings.isFrontCamera, settings.videoQuality, settings.flashMode, hasCameraPermission) {
    previewViewRef?.let { pv ->
      if (hasCameraPermission) {
        cameraController.startCamera(
          previewView = pv,
          isFront = settings.isFrontCamera,
          isVideoMode = settings.isVideoMode,
          videoQualityName = settings.videoQuality,
          flashMode = settings.flashMode
        ) { available ->
          isCameraXActive = available
        }
      }
    }
  }

  // Update hardware flash mode
  LaunchedEffect(settings.flashMode) {
    cameraController.setFlashMode(settings.flashMode)
  }

  // Update exposure whenever setting changes
  LaunchedEffect(settings.exposureCompensation) {
    cameraController.setExposure(settings.exposureCompensation)
  }

  // Recording duration timer
  LaunchedEffect(isRecordingVideo) {
    if (isRecordingVideo) {
      recordingDurationSeconds = 0
      while (isRecordingVideo) {
        kotlinx.coroutines.delay(1000)
        recordingDurationSeconds++
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      cameraController.shutdown()
    }
  }

  // Combine film preset params with live exposure and live temperature
  val liveParams = remember(params, settings.exposureCompensation, settings.liveTemperature) {
    params.copy(
      exposure = (params.exposure + settings.exposureCompensation).coerceIn(-1.0f, 1.0f),
      temperature = (params.temperature + settings.liveTemperature).coerceIn(-1.0f, 1.0f)
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CozyObsidian)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 560.dp),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Top Quick Setting Controls
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // In Video Mode: Video Quality Pill; In Photo Mode: Aspect Ratio & Resolution
        if (settings.isVideoMode) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF8A3022))
              .clickable { showVideoSettingsSheet = true }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.VideoSettings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${settings.videoQuality} • ${settings.videoFps}fps",
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              )
            }
          }
        } else {
          // Aspect Ratio Pill (3:4, 1:1, 16:9)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(CozyCharcoalSurface)
              .clickable(onClick = onCycleAspectRatio)
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = settings.aspectRatio,
              color = CozyCream,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          // Resolution Pill (12MP, 24MP, 8MP)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(CozyCharcoalSurface)
              .clickable(onClick = onCycleResolution)
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = settings.resolution,
              color = CozyCream,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Flash Toggle
        IconButton(onClick = onCycleFlash, modifier = Modifier.size(36.dp)) {
          Icon(
            imageVector = when (settings.flashMode) {
              "on" -> Icons.Default.FlashOn
              "auto" -> Icons.Default.FlashAuto
              else -> Icons.Default.FlashOff
            },
            contentDescription = "Flash",
            tint = if (settings.flashMode != "off") CozyAmberGold else CozyMutedText,
            modifier = Modifier.size(20.dp)
          )
        }

        // Timer Toggle
        IconButton(onClick = onCycleTimer, modifier = Modifier.size(36.dp)) {
          Icon(
            imageVector = when (settings.timerSeconds) {
              3 -> Icons.Default.Timer3
              10 -> Icons.Default.Timer10
              else -> Icons.Default.Timer
            },
            contentDescription = "Timer",
            tint = if (settings.timerSeconds > 0) CozyAmberGold else CozyMutedText,
            modifier = Modifier.size(20.dp)
          )
        }

        // Grid Toggle
        IconButton(onClick = onToggleGrid, modifier = Modifier.size(36.dp)) {
          Icon(
            imageVector = if (settings.gridEnabled) Icons.Default.GridOn else Icons.Default.GridOff,
            contentDescription = "Grid",
            tint = if (settings.gridEnabled) CozyCream else CozyMutedText,
            modifier = Modifier.size(20.dp)
          )
        }

        // Settings Dialog
        IconButton(onClick = onOpenSettings, modifier = Modifier.size(36.dp)) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = CozyMutedText,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // 2. Viewfinder Frame
      val aspectModifier = when (settings.aspectRatio) {
        "1:1" -> Modifier.aspectRatio(1f)
        "16:9" -> Modifier.aspectRatio(9f / 16f)
        else -> Modifier.aspectRatio(3f / 4f)
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
          .then(aspectModifier)
          .clip(RoundedCornerShape(26.dp))
          .background(CozyCharcoal)
          .border(1.dp, CozyBorder.copy(alpha = 0.5f), RoundedCornerShape(26.dp)),
        contentAlignment = Alignment.Center
      ) {
        if (hasCameraPermission) {
          AndroidView(
            factory = { ctx ->
              PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                previewViewRef = this
                cameraController.startCamera(
                  previewView = this,
                  isFront = settings.isFrontCamera,
                  isVideoMode = settings.isVideoMode,
                  videoQualityName = settings.videoQuality,
                  flashMode = settings.flashMode
                ) { available ->
                  isCameraXActive = available
                }
              }
            },
            modifier = Modifier.fillMaxSize()
          )
        }

        // Fallback or ambient viewfinder image when camera is unavailable or sensor is inactive
        if (!hasCameraPermission || !isCameraXActive) {
          Image(
            painter = painterResource(id = R.drawable.img_sample_viewfinder),
            contentDescription = "Viewfinder Frame",
            modifier = Modifier
              .fillMaxSize()
              .graphicsLayer {
                if (settings.isFrontCamera) scaleX = -1f
              },
            contentScale = ContentScale.Crop
          )
        }

        // Real-time Film grading & Vignette & Date stamp & Grid overlay!
        // Uses combined live exposure and temperature so viewfinder immediately responds to sliders!
        FilmOverlayEffect(
          params = liveParams,
          preset = activePreset,
          showGrid = settings.gridEnabled
        )

        // Front selfie badge indicator
        if (settings.isFrontCamera) {
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(start = 14.dp, top = 14.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xB3191817))
              .border(0.8.dp, CozyAmberGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "FRONT SELFIE",
              color = CozyAmberGold,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Viewfinder Zoom Pills (0.5x, 1x, 2x) at bottom center
        Row(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x99191817))
            .padding(horizontal = 6.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          listOf(0.5f to "0.5x", 1.0f to "1x", 2.0f to "2x").forEach { (level, text) ->
            val isSelected = settings.zoomLevel == level
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) CozyCream else Color.Transparent)
                .clickable {
                  onSetZoom(level)
                  cameraController.setZoom(level - 0.5f)
                }
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = text,
                color = if (isSelected) CozyObsidian else CozyCream,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        // Self-timer countdown overlay
        val currentCountdown = activeTimerCountdown ?: timerCountdown
        if (currentCountdown != null) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0x88000000))
              .clickable {
                timerJob?.cancel()
                timerJob = null
                activeTimerCountdown = null
              },
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = currentCountdown.toString(),
                color = CozyAmberGold,
                fontSize = 84.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "Tap to cancel timer",
                color = CozyCream.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        // Recording indicator badge in video mode
        if (isRecordingVideo) {
          Box(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .padding(top = 16.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xD9B00020))
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(Color.White)
              )
              val minutes = recordingDurationSeconds / 60
              val seconds = recordingDurationSeconds % 60
              Text(
                text = "REC %02d:%02d".format(minutes, seconds),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // 3. Middle Section: Temperature and Exposure Sliders Bar!
      CameraToneControlBar(
        exposure = settings.exposureCompensation,
        temperature = settings.liveTemperature,
        onExposureChange = { ev ->
          onSetExposure(ev)
          cameraController.setExposure(ev)
        },
        onTemperatureChange = { temp ->
          onSetTemperature(temp)
        },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
      )

      // 4. Lower Control Section (Preset card, Photo/Video mode, Shutter bar)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Selected Preset Card Pill (tapping opens Presets screen!)
        Box(
          modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(20.dp))
            .background(CozyCharcoalElevated)
            .border(1.dp, CozyBorder.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable(onClick = onOpenPresets)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("preset_selector_card")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              VintageCameraIllustration(
                type = activePreset.cameraType,
                size = 40.dp
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = activePreset.name,
                  color = CozyCream,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = activePreset.subtitle,
                  color = CozyMutedText,
                  fontSize = 11.sp
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CozyCharcoalSurface)
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = "Change",
                color = CozyAmberGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mode switch: PHOTO / VIDEO
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CozyCharcoalSurface)
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (!settings.isVideoMode) CozyCream else Color.Transparent)
              .clickable { if (settings.isVideoMode) onToggleVideoMode() }
              .padding(horizontal = 18.dp, vertical = 6.dp)
          ) {
            Text(
              text = "PHOTO",
              color = if (!settings.isVideoMode) CozyObsidian else CozyMutedText,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (settings.isVideoMode) Color(0xFFE05A47) else Color.Transparent)
              .clickable { if (!settings.isVideoMode) onToggleVideoMode() }
              .padding(horizontal = 18.dp, vertical = 6.dp)
          ) {
            Text(
              text = "VIDEO",
              color = if (settings.isVideoMode) Color.White else CozyMutedText,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Action Bar: Gallery Thumbnail, Shutter Button, Media Picker, Flip Camera
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Latest photo thumbnail
          val latestBitmap = remember(latestPhoto?.uri) {
            latestPhoto?.uri?.let { path ->
              val f = File(path)
              if (f.exists() && !path.endsWith(".mp4")) BitmapFactory.decodeFile(f.absolutePath) else null
            }
          }

          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(CozyCharcoalSurface)
              .border(1.5.dp, CozyBorder, CircleShape)
              .clickable(onClick = onOpenGallery)
              .testTag("gallery_thumbnail_button"),
            contentAlignment = Alignment.Center
          ) {
            if (latestBitmap != null) {
              Image(
                bitmap = latestBitmap.asImageBitmap(),
                contentDescription = "Gallery",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              VintageCameraIllustration(
                type = activePreset.cameraType,
                size = 30.dp
              )
            }
          }

          // Shutter Button
          ShutterButton(
            isVideoMode = settings.isVideoMode,
            isRecording = isRecordingVideo,
            onClick = {
              val executeShutter: () -> Unit = {
                if (settings.isVideoMode) {
                  if (isRecordingVideo) {
                    cameraController.stopRecording()
                    isRecordingVideo = false
                  } else {
                    val videoFile = File(context.cacheDir, "COZY_VID_${System.currentTimeMillis()}.mp4")
                    isRecordingVideo = true
                    cameraController.startRecording(
                      outputFile = videoFile,
                      enableAudio = settings.videoAudioEnabled,
                      onVideoSaved = { savedFile ->
                        isRecordingVideo = false
                        onVideoRecorded(savedFile)
                      },
                      onError = { err ->
                        isRecordingVideo = false
                        onVideoRecorded(videoFile)
                      }
                    )
                  }
                } else {
                  if (settings.flashMode == "on" || settings.isFrontCamera) {
                    isScreenFlashActive = true
                  }
                  cameraController.takePicture(
                    previewView = previewViewRef,
                    onSuccess = { bmp ->
                      isScreenFlashActive = false
                      onShutterClick(bmp)
                    }
                  )
                }
              }

              if (settings.timerSeconds > 0 && !isRecordingVideo) {
                if (activeTimerCountdown != null) {
                  // Cancel countdown
                  timerJob?.cancel()
                  timerJob = null
                  activeTimerCountdown = null
                } else {
                  timerJob = coroutineScope.launch {
                    for (i in settings.timerSeconds downTo 1) {
                      activeTimerCountdown = i
                      kotlinx.coroutines.delay(1000)
                    }
                    activeTimerCountdown = null
                    executeShutter()
                  }
                }
              } else {
                executeShutter()
              }
            }
          )

          // Right: Photo Picker from Device
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(CozyCharcoalSurface)
              .border(1.2.dp, CozyBorder, CircleShape)
              .clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AddPhotoAlternate,
              contentDescription = "Import Photo",
              tint = CozyCream,
              modifier = Modifier.size(22.dp)
            )
          }

          // Far Right: Flip Camera button
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(CozyCharcoalSurface)
              .border(1.2.dp, CozyBorder, CircleShape)
              .clickable(onClick = onToggleCameraFacing),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Cameraswitch,
              contentDescription = "Flip Camera",
              tint = CozyCream,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }
    }

    // Video Settings Sheet Modal
    if (showVideoSettingsSheet) {
      VideoSettingsSheet(
        settings = settings,
        onUpdateQuality = onUpdateVideoQuality,
        onUpdateFps = onUpdateVideoFps,
        onToggleAudio = onToggleVideoAudio,
        onSelectScene = onSelectSampleScene,
        onDismiss = { showVideoSettingsSheet = false }
      )
    }

    // Shutter flash whiteout / warm screen flash effect animation
    AnimatedVisibility(
      visible = isScreenFlashActive || shutterFlashAnim,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.fillMaxSize()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(if (isScreenFlashActive) Color(0xFFFFF7E6) else Color.White)
      )
    }
  }
}
