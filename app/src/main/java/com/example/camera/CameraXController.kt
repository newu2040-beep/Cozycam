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

  var isCameraAvailable: Boolean = false
    private set

  val isRecording: Boolean
    get() = currentRecording != null

  fun startCamera(
    previewView: PreviewView,
    isFront: Boolean = false,
    onReady: (Boolean) -> Unit = {}
  ) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
      try {
        cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
          it.surfaceProvider = previewView.surfaceProvider
        }

        imageCapture = ImageCapture.Builder()
          .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
          .build()

        val recorder = Recorder.Builder()
          .setQualitySelector(QualitySelector.from(Quality.HD, FallbackStrategy.higherQualityOrLowerThan(Quality.SD)))
          .build()
        videoCapture = VideoCapture.withOutput(recorder)

        val cameraSelector = if (isFront) {
          CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
          CameraSelector.DEFAULT_BACK_CAMERA
        }

        cameraProvider?.unbindAll()

        if (cameraProvider?.hasCamera(cameraSelector) == true) {
          try {
            camera = cameraProvider?.bindToLifecycle(
              lifecycleOwner,
              cameraSelector,
              preview,
              imageCapture,
              videoCapture
            )
          } catch (bindAllEx: Exception) {
            // Fallback to preview + imageCapture if 3 use cases not supported on hardware
            camera = cameraProvider?.bindToLifecycle(
              lifecycleOwner,
              cameraSelector,
              preview,
              imageCapture
            )
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
    onVideoSaved: (File) -> Unit,
    onError: (Exception) -> Unit
  ) {
    val vc = videoCapture ?: run {
      onError(IllegalStateException("Video capture not ready"))
      return
    }

    val outputOptions = FileOutputOptions.Builder(outputFile).build()
    var pendingRecording = vc.output.prepareRecording(context, outputOptions)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
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
  }

  fun stopRecording() {
    currentRecording?.stop()
    currentRecording = null
  }

  fun setZoom(zoomLevel: Float) {
    camera?.cameraControl?.setLinearZoom(zoomLevel.coerceIn(0.0f, 1.0f))
  }

  fun setFlashMode(mode: String) {
    val flash = when (mode.lowercase()) {
      "on" -> ImageCapture.FLASH_MODE_ON
      "auto" -> ImageCapture.FLASH_MODE_AUTO
      else -> ImageCapture.FLASH_MODE_OFF
    }
    imageCapture?.flashMode = flash
    if (mode.lowercase() == "torch") {
      camera?.cameraControl?.enableTorch(true)
    } else {
      camera?.cameraControl?.enableTorch(false)
    }
  }

  fun takePicture(
    onSuccess: (Bitmap) -> Unit,
    onError: (Exception) -> Unit
  ) {
    val capture = imageCapture
    if (capture == null || !isCameraAvailable) {
      onError(IllegalStateException("Camera not ready"))
      return
    }

    capture.takePicture(
      cameraExecutor,
      object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
          try {
            val bitmap = imageProxyToBitmap(image)
            image.close()
            ContextCompat.getMainExecutor(context).execute {
              onSuccess(bitmap)
            }
          } catch (e: Exception) {
            image.close()
            ContextCompat.getMainExecutor(context).execute {
              onError(e)
            }
          }
        }

        override fun onError(exception: ImageCaptureException) {
          ContextCompat.getMainExecutor(context).execute {
            onError(exception)
          }
        }
      }
    )
  }

  private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val plane = image.planes[0]
    val buffer = plane.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    val rotation = image.imageInfo.rotationDegrees
    return if (rotation != 0) {
      val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
      Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
      bitmap
    }
  }

  fun shutdown() {
    cameraExecutor.shutdown()
  }
}
