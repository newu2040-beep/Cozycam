package com.example.data.media

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MediaStoreManager(private val context: Context) {

  suspend fun saveBitmapToMediaStore(
    bitmap: Bitmap,
    filename: String = "COZY_${System.currentTimeMillis()}.jpg"
  ): Uri? = withContext(Dispatchers.IO) {
    val contentValues = ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
      put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CozyCam")
        put(MediaStore.MediaColumns.IS_PENDING, 1)
      }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let { destUri ->
      try {
        resolver.openOutputStream(destUri)?.use { stream: OutputStream ->
          bitmap.compress(Bitmap.CompressFormat.JPEG, 96, stream)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          contentValues.clear()
          contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
          resolver.update(destUri, contentValues, null, null)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
      }
    }

    // Also write a local copy in app files directory for guaranteed instant offline retrieval
    saveLocally(bitmap, filename)

    return@withContext uri
  }

  suspend fun saveVideoToMediaStore(
    videoFile: File,
    filename: String = "COZY_${System.currentTimeMillis()}.mp4"
  ): Uri? = withContext(Dispatchers.IO) {
    val contentValues = ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
      put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/CozyCam")
        put(MediaStore.MediaColumns.IS_PENDING, 1)
      }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let { destUri ->
      try {
        resolver.openOutputStream(destUri)?.use { outStream ->
          videoFile.inputStream().use { inStream ->
            inStream.copyTo(outStream)
          }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          contentValues.clear()
          contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
          resolver.update(destUri, contentValues, null, null)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
      }
    }

    return@withContext uri
  }

  suspend fun saveLocally(bitmap: Bitmap, filename: String): File = withContext(Dispatchers.IO) {
    val directory = File(context.filesDir, "cozycam_photos")
    if (!directory.exists()) directory.mkdirs()
    val file = File(directory, filename)
    FileOutputStream(file).use { out ->
      bitmap.compress(Bitmap.CompressFormat.JPEG, 96, out)
    }
    file
  }

  fun shareImage(bitmap: Bitmap, caption: String = "Captured with CozyCam 🎞️") {
    try {
      val imagesFolder = File(context.cacheDir, "images")
      if (!imagesFolder.exists()) imagesFolder.mkdirs()
      val file = File(imagesFolder, "share_${System.currentTimeMillis()}.jpg")
      FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
      }

      val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_TEXT, caption)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      val chooser = Intent.createChooser(shareIntent, "Share CozyCam Photo")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
