package com.example.ui.components

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.data.room.PhotoEntity
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun VideoPlayerDialog(
  photo: PhotoEntity,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var isPlaying by remember { mutableStateOf(true) }
  var currentPosition by remember { mutableIntStateOf(0) }
  var totalDuration by remember { mutableIntStateOf(0) }
  var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
  var isControlsVisible by remember { mutableStateOf(true) }

  val videoFile = remember(photo.uri) { File(photo.uri) }
  val videoUri = remember(photo.uri) {
    if (videoFile.exists()) {
      try {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", videoFile)
      } catch (e: Exception) {
        Uri.fromFile(videoFile)
      }
    } else {
      Uri.parse(photo.uri)
    }
  }

  // Periodic position updater
  LaunchedEffect(isPlaying) {
    while (isPlaying) {
      videoViewRef?.let { vv ->
        if (vv.isPlaying) {
          currentPosition = vv.currentPosition
          if (vv.duration > 0) totalDuration = vv.duration
        }
      }
      delay(300)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xE60D0C0B)),
      color = Color.Transparent
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1A17)),
          elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE05A47)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = photo.presetName,
                    color = Color(0xFFF3EDE2),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                  Text(
                    text = "Vintage Motion Reel",
                    color = Color(0xFFD4A373),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = {
                    try {
                      val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "video/mp4"
                        putExtra(Intent.EXTRA_STREAM, videoUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                      }
                      context.startActivity(Intent.createChooser(shareIntent, "Share Vintage Video"))
                    } catch (e: Exception) {
                      // fallback
                    }
                  }
                ) {
                  Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Video",
                    tint = Color(0xFFF3EDE2)
                  )
                }
                IconButton(onClick = onDismiss) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFFF3EDE2)
                  )
                }
              }
            }

            // Video Player Area
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .background(Color.Black)
                .clickable { isControlsVisible = !isControlsVisible },
              contentAlignment = Alignment.Center
            ) {
              AndroidView(
                factory = { ctx ->
                  VideoView(ctx).apply {
                    setVideoURI(videoUri)
                    setOnPreparedListener { mp ->
                      mp.isLooping = true
                      totalDuration = mp.duration
                      start()
                      isPlaying = true
                    }
                    setOnCompletionListener {
                      isPlaying = false
                    }
                    videoViewRef = this
                  }
                },
                modifier = Modifier.fillMaxSize()
              )

              // Retro Camcorder HUD Overlay
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(16.dp)
              ) {
                // Top Left: REC badge
                Row(
                  modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color(0x99000000), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(if (isPlaying) Color.Red else Color.Gray)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (isPlaying) "PLAY" else "PAUSE",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                // Top Right: SP / HQ badge
                Text(
                  text = "SP 1080p",
                  color = Color(0xFFE4D9C8),
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color(0x99000000), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                // Center Play/Pause button on tap
                if (isControlsVisible) {
                  Box(
                    modifier = Modifier
                      .align(Alignment.Center)
                      .size(64.dp)
                      .clip(CircleShape)
                      .background(Color(0xAA000000))
                      .border(2.dp, Color(0xFFD4A373), CircleShape)
                      .clickable {
                        videoViewRef?.let { vv ->
                          if (vv.isPlaying) {
                            vv.pause()
                            isPlaying = false
                          } else {
                            vv.start()
                            isPlaying = true
                          }
                        }
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                      contentDescription = if (isPlaying) "Pause" else "Play",
                      tint = Color(0xFFF3EDE2),
                      modifier = Modifier.size(36.dp)
                    )
                  }
                }

                // Bottom Left: Timecode
                val currentSec = currentPosition / 1000
                val totalSec = totalDuration / 1000
                val timecode = String.format("%02d:%02d / %02d:%02d", currentSec / 60, currentSec % 60, totalSec / 60, totalSec % 60)
                Text(
                  text = timecode,
                  color = Color(0xFFFFB347),
                  fontFamily = FontFamily.Monospace,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color(0x99000000), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            // Bottom Scrubber Bar
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
              val progress = if (totalDuration > 0) currentPosition.toFloat() / totalDuration else 0f
              Slider(
                value = progress.coerceIn(0f, 1f),
                onValueChange = { newProgress ->
                  videoViewRef?.let { vv ->
                    val seekTo = (newProgress * totalDuration).toInt()
                    vv.seekTo(seekTo)
                    currentPosition = seekTo
                  }
                },
                colors = SliderDefaults.colors(
                  thumbColor = Color(0xFFE05A47),
                  activeTrackColor = Color(0xFFD4A373),
                  inactiveTrackColor = Color(0xFF33302B)
                ),
                modifier = Modifier.fillMaxWidth()
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Analog Tape Audio: Stereo Hi-Fi",
                  color = Color(0xFF9E988F),
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = "CozyCam Reel",
                  color = Color(0xFF9E988F),
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }
        }
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      videoViewRef?.stopPlayback()
    }
  }
}
