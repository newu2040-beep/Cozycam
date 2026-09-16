package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.CameraSettings
import com.example.ui.theme.CozyAmberGold
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyCharcoal
import com.example.ui.theme.CozyCharcoalElevated
import com.example.ui.theme.CozyCharcoalSurface
import com.example.ui.theme.CozyCream
import com.example.ui.theme.CozyMutedText
import com.example.ui.theme.CozyObsidian
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSettingsSheet(
  settings: CameraSettings,
  onUpdateQuality: (String) -> Unit,
  onUpdateFps: (Int) -> Unit,
  onToggleAudio: (Boolean) -> Unit,
  onSelectScene: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var customFpsSlider by remember(settings.videoFps) {
    mutableFloatStateOf(settings.videoFps.toFloat().coerceIn(12f, 120f))
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CozyCharcoal,
    contentColor = CozyCream,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 12.dp)
          .width(44.dp)
          .height(4.dp)
          .clip(CircleShape)
          .background(CozyMutedText.copy(alpha = 0.5f))
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .padding(bottom = 36.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(Color(0xFFE05A47)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Videocam,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Video Resolution & FPS",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = CozyCream
            )
            Text(
              text = "Hardware profiles & custom motion cadence",
              fontSize = 12.sp,
              color = CozyMutedText
            )
          }
        }
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = CozyMutedText
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 1. Resolution Selection (Hardware supported & vintage options)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "RECORDING RESOLUTION",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = CozyAmberGold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )
        Text(
          text = "Supported Profiles",
          fontSize = 11.sp,
          color = CozyMutedText
        )
      }
      Spacer(modifier = Modifier.height(10.dp))

      val resolutions = listOf(
        Triple("4K UHD", "3840x2160 • High Bitrate Ultra HD", "Crisp Master"),
        Triple("1080p FHD", "1920x1080 • Standard Full HD 16:9", "Supported"),
        Triple("720p HD", "1280x720 • Fast Processing HD", "Supported"),
        Triple("480p SD", "640x480 • Authentic VHS Camcorder", "Retro Standard")
      )

      resolutions.forEach { (resKey, desc, badge) ->
        val isSelected = settings.videoQuality == resKey
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) CozyCharcoalElevated else CozyCharcoalSurface)
            .border(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) CozyAmberGold else CozyBorder.copy(alpha = 0.4f),
              shape = RoundedCornerShape(14.dp)
            )
            .clickable { onUpdateQuality(resKey) }
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = resKey,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) CozyCream else CozyCream.copy(alpha = 0.85f)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) CozyAmberGold.copy(alpha = 0.2f) else CozyCharcoal)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = badge,
                  fontSize = 10.sp,
                  color = if (isSelected) CozyAmberGold else CozyMutedText,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
            Text(
              text = desc,
              fontSize = 11.sp,
              color = CozyMutedText
            )
          }

          if (isSelected) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = CozyAmberGold,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // 2. Custom Frame Rate (FPS) Selector
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = CozyAmberGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "FRAME RATE (FPS)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CozyAmberGold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
        }
        Text(
          text = "${settings.videoFps} FPS Active",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = CozyCream,
          fontFamily = FontFamily.Monospace
        )
      }
      Spacer(modifier = Modifier.height(10.dp))

      // Preset FPS quick pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(
          15 to "15 FPS",
          24 to "24 FPS",
          30 to "30 FPS",
          60 to "60 FPS",
          120 to "120 FPS"
        ).forEach { (fpsVal, label) ->
          val isSelected = settings.videoFps == fpsVal
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) CozyAmberGold else CozyCharcoalSurface)
              .border(
                1.dp,
                if (isSelected) CozyAmberGold else CozyBorder.copy(alpha = 0.4f),
                RoundedCornerShape(10.dp)
              )
              .clickable {
                customFpsSlider = fpsVal.toFloat()
                onUpdateFps(fpsVal)
              }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) CozyObsidian else CozyCream
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Custom continuous FPS slider & stepper
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(CozyCharcoalSurface)
          .border(1.dp, CozyBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Custom Frame Rate Slider",
              color = CozyCream,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = when {
                settings.videoFps <= 18 -> "Vintage silent 8mm crank motion"
                settings.videoFps in 24..25 -> "Cinematic 35mm motion blur"
                settings.videoFps in 30..48 -> "Classic broadcast video"
                settings.videoFps in 50..60 -> "High fluid real-time action"
                else -> "High speed / slow motion capture"
              },
              color = CozyMutedText,
              fontSize = 11.sp
            )
          }

          // Stepper (+ / -)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CozyCharcoalElevated)
                .clickable {
                  val newFps = (settings.videoFps - 1).coerceAtLeast(12)
                  customFpsSlider = newFps.toFloat()
                  onUpdateFps(newFps)
                },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease FPS",
                tint = CozyCream,
                modifier = Modifier.size(16.dp)
              )
            }
            Text(
              text = "${settings.videoFps}",
              color = CozyAmberGold,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.padding(horizontal = 4.dp)
            )
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CozyCharcoalElevated)
                .clickable {
                  val newFps = (settings.videoFps + 1).coerceAtMost(120)
                  customFpsSlider = newFps.toFloat()
                  onUpdateFps(newFps)
                },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase FPS",
                tint = CozyCream,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
          value = customFpsSlider,
          onValueChange = {
            customFpsSlider = it
          },
          onValueChangeFinished = {
            val rounded = customFpsSlider.roundToInt()
            onUpdateFps(rounded)
          },
          valueRange = 12f..120f,
          steps = 107,
          colors = SliderDefaults.colors(
            thumbColor = CozyAmberGold,
            activeTrackColor = CozyAmberGold,
            inactiveTrackColor = CozyCharcoalElevated
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "12 FPS", fontSize = 10.sp, color = CozyMutedText, fontFamily = FontFamily.Monospace)
          Text(text = "24 FPS (Cine)", fontSize = 10.sp, color = CozyMutedText, fontFamily = FontFamily.Monospace)
          Text(text = "60 FPS", fontSize = 10.sp, color = CozyMutedText, fontFamily = FontFamily.Monospace)
          Text(text = "120 FPS", fontSize = 10.sp, color = CozyMutedText, fontFamily = FontFamily.Monospace)
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3. Audio Recording Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(CozyCharcoalSurface)
          .border(1.dp, CozyBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (settings.videoAudioEnabled) Icons.Default.Mic else Icons.Default.MicOff,
            contentDescription = null,
            tint = if (settings.videoAudioEnabled) CozyAmberGold else CozyMutedText,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Microphone Audio",
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = CozyCream
            )
            Text(
              text = if (settings.videoAudioEnabled) "High fidelity stereo track" else "Mute (Silent Vintage Reel)",
              fontSize = 11.sp,
              color = CozyMutedText
            )
          }
        }
        Switch(
          checked = settings.videoAudioEnabled,
          onCheckedChange = onToggleAudio,
          colors = SwitchDefaults.colors(
            checkedThumbColor = CozyObsidian,
            checkedTrackColor = CozyAmberGold,
            uncheckedThumbColor = CozyMutedText,
            uncheckedTrackColor = CozyCharcoal
          )
        )
      }
    }
  }
}
