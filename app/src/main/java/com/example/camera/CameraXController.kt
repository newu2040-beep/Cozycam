package com.example.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXController(
  private val context: Context,
  private val lifecycleOwner: LifecycleOwner
) {
  private var cameraProvider: ProcessCameraProvider? = null
  private var imageCapture: ImageCapture? = null
  private var videoCapture: VideoCapture<Recorder>? = null
  private var currentRecording: Recording? = null
  private var camera: Camera? = null
  private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
  private var currentPreviewView: PreviewView? = null

  private var isFrontActive: Boolean = false
  private var currentFlashMode: String = "off"

  var isCameraAvailable: Boolean = false
    private set

  val isRecording: Boolean
    get() = currentRecording != null

  fun startCamera(
    previewView: PreviewView,
    isFront: Boolean = false,
    isVideoMode: Boolean = false,
    videoQualityName: String = "1080p FHD",
    flashMode: String = currentFlashMode,
    onReady: (Boolean) -> Unit = {}
  ) {
    this.currentPreviewView = previewView
    this.isFrontActive = isFront
    this.currentFlashMode = flashMode

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
      try {
        cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
          it.surfaceProvider = previewView.surfaceProvider
        }

        val desired = if (isFront) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
        val cameraSelector = when {
          cameraProvider?.hasCamera(desired) == true -> desired
          cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) == true -> CameraSelector.DEFAULT_BACK_CAMERA
          cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) == true -> CameraSelector.DEFAULT_FRONT_CAMERA
          else -> desired
        }

        cameraProvider?.unbindAll()

        if (cameraProvider?.hasCamera(cameraSelector) == true) {
          if (isVideoMode) {
            val quality = when (videoQualityName) {
              "4K UHD" -> Quality.UHD
              "720p HD" -> Quality.HD
              "480p SD" -> Quality.SD
              else -> Quality.FHD
            }
            val qualitySelector = QualitySelector.from(
              quality,
              FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
            )
            val recorder = Recorder.Builder()
              .setQualitySelector(qualitySelector)
              .build()
            videoCapture = VideoCapture.withOutput(recorder)
            imageCapture = null

            camera = cameraProvider?.bindToLifecycle(
              lifecycleOwner,
              cameraSelector,
              preview,
              videoCapture
            )
          } else {
            val flash = when (currentFlashMode.lowercase()) {
              "on" -> ImageCapture.FLASH_MODE_ON
              "auto" -> ImageCapture.FLASH_MODE_AUTO
              else -> ImageCapture.FLASH_MODE_OFF
            }
            imageCapture = ImageCapture.Builder()
              .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
              .setFlashMode(flash)
              .build()
            videoCapture = null

            camera = cameraProvider?.bindToLifecycle(
              lifecycleOwner,
              cameraSelector,
              preview,
              imageCapture
            )
          }

          if (currentFlashMode.lowercase() == "torch") {
            try {
              if (camera?.cameraInfo?.hasFlashUnit() == true) {
                camera?.cameraControl?.enableTorch(true)
              }
            } catch (e: Exception) {}
          }

          isCameraAvailable = true
          onReady(true)
        } else {
          isCameraAvailable = false
          onReady(false)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        isCameraAvailable = false
        onReady(false)
      }
    }, ContextCompat.getMainExecutor(context))
  }

  fun startRecording(
    outputFile: File,
    enableAudio: Boolean = true,
    onVideoSaved: (File) -> Unit,
    onError: (Exception) -> Unit
  ) {
    val vc = videoCapture ?: run {
      onError(IllegalStateException("Video capture not initialized"))
      return
    }

    try {
      val outputOptions = FileOutputOptions.Builder(outputFile).build()
      var pendingRecording = vc.output.prepareRecording(context, outputOptions)

      if (enableAudio && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
        pendingRecording = pendingRecording.withAudioEnabled()
      }

      currentRecording = pendingRecording.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
        when (recordEvent) {
          is VideoRecordEvent.Finalize -> {
            currentRecording = null
            if (recordEvent.hasError()) {
              val cause = recordEvent.cause
              val errorEx = if (cause is Exception) cause else Exception("Video recording failed (code: ${recordEvent.error})", cause)
              onError(errorEx)
            } else {
              onVideoSaved(outputFile)
            }
          }
          else -> {}
        }
      }
    } catch (e: Exception) {
      currentRecording = null
      onError(e)
    }
  }

  fun stopRecording() {
    currentRecording?.stop()
    currentRecording = null
  }

  fun setZoom(zoomLevel: Float) {
    camera?.cameraControl?.setLinearZoom(zoomLevel.coerceIn(0.0f, 1.0f))
  }

  fun setExposure(ev: Float) {
    try {
      val state = camera?.cameraInfo?.exposureState ?: return
      if (state.isExposureCompensationSupported) {
        val range = state.exposureCompensationRange
        val step = state.exposureCompensationStep.toFloat()
        val targetIndex = (ev / step).toInt().coerceIn(range.lower, range.upper)
        camera?.cameraControl?.setExposureCompensationIndex(targetIndex)
      }
    } catch (e: Exception) {
      // Ignore if exposure compensation is unsupported on hardware
    }
  }

  fun setFlashMode(mode: String) {
    currentFlashMode = mode
    val flash = when (mode.lowercase()) {
      "on" -> ImageCapture.FLASH_MODE_ON
      "auto" -> ImageCapture.FLASH_MODE_AUTO
      else -> ImageCapture.FLASH_MODE_OFF
    }
    imageCapture?.flashMode = flash
    try {
      if (camera?.cameraInfo?.hasFlashUnit() == true) {
        camera?.cameraControl?.enableTorch(mode.lowercase() == "torch")
      }
    } catch (e: Exception) {}
  }

  fun takePicture(
    previewView: PreviewView? = currentPreviewView,
    onSuccess: (Bitmap) -> Unit,
    onError: (Exception) -> Unit
  ) {
    val capture = imageCapture
    if (capture == null || !isCameraAvailable) {
      val pvBitmap = previewView?.bitmap
      if (pvBitmap != null && !isBitmapBlank(pvBitmap)) {
        onSuccess(pvBitmap)
      } else {
        onError(IllegalStateException("Camera capture not available"))
      }
      return
    }

    capture.takePicture(
      cameraExecutor,
      object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
          try {
            val bitmap = imageProxyToBitmap(image)
            image.close()

            val finalBitmap = if (isBitmapBlank(bitmap)) {
              val fallback = previewView?.bitmap
              if (fallback != null && !isBitmapBlank(fallback)) fallback else bitmap
            } else {
              bitmap
            }

            ContextCompat.getMainExecutor(context).execute {
              onSuccess(finalBitmap)
            }
          } catch (e: Exception) {
            image.close()
            ContextCompat.getMainExecutor(context).execute {
              val fallback = previewView?.bitmap
              if (fallback != null && !isBitmapBlank(fallback)) {
                onSuccess(fallback)
              } else {
                onError(e)
              }
            }
          }
        }

        override fun onError(exception: ImageCaptureException) {
          ContextCompat.getMainExecutor(context).execute {
            val fallback = previewView?.bitmap
            if (fallback != null && !isBitmapBlank(fallback)) {
              onSuccess(fallback)
            } else {
              onError(exception)
            }
          }
        }
      }
    )
  }

  private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val bitmap = try {
      image.toBitmap()
    } catch (e: Exception) {
      val plane = image.planes[0]
      val buffer = plane.buffer
      val bytes = ByteArray(buffer.remaining())
      buffer.get(bytes)
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: throw IllegalStateException("Could not decode image bytes")
    }

    val rotation = image.imageInfo.rotationDegrees
    val matrix = Matrix().apply {
      if (rotation != 0) postRotate(rotation.toFloat())
      if (isFrontActive) postScale(-1f, 1f) // Mirror selfie to match preview
    }

    return if (!matrix.isIdentity) {
      Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
      bitmap
    }
  }

  /**
   * Fast check to see if an image is all black / blank (common in emulators with virtual cameras)
   */
  fun isBitmapBlank(bitmap: Bitmap): Boolean {
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
    // If fewer than 2% of pixels have any luminance, consider it blank
    return totalSampled > 0 && (nonBlackPixels.toFloat() / totalSampled) < 0.02f
  }

  fun shutdown() {
    cameraExecutor.shutdown()
  }
}
