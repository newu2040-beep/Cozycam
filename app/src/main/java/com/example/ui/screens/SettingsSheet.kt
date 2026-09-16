package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
  settings: CameraSettings,
  onDismiss: () -> Unit,
  onToggleGrid: (Boolean) -> Unit,
  onToggleHaptics: (Boolean) -> Unit,
  onToggleDateStamp: (Boolean) -> Unit,
  onSetTimer: (Int) -> Unit,
  onSetResolution: (String) -> Unit,
  onSetFlashMode: (String) -> Unit,
  onToggleCameraFacing: () -> Unit,
  onSetAspectRatio: (String) -> Unit,
  onOpenVideoSettings: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CozyCharcoal,
    contentColor = CozyCream,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 12.dp)
          .size(width = 40.dp, height = 4.dp)
          .clip(CircleShape)
          .background(CozyBorder)
      )
    }
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 8.dp)
        .padding(bottom = 36.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Camera Preferences",
            color = CozyCream,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Fine-tune hardware, timing, and optics",
            color = CozyMutedText,
            fontSize = 12.sp
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = CozyMutedText
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 1. Rule of Thirds Grid
      SettingToggleRow(
        title = "Rule of Thirds Grid",
        description = "Align photos with photographic grid guides",
        checked = settings.gridEnabled,
        onCheckedChange = { onToggleGrid(it) }
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Haptic Feedback
      SettingToggleRow(
        title = "Haptic Shutter",
        description = "Tactile mechanical vibration on press",
        checked = settings.hapticsEnabled,
        onCheckedChange = { onToggleHaptics(it) }
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Vintage Date Stamp
      SettingToggleRow(
        title = "Vintage Date Stamp",
        description = "Print retro amber timestamp on bottom corner",
        checked = settings.dateStampEnabled,
        onCheckedChange = { onToggleDateStamp(it) }
      )

      Spacer(modifier = Modifier.height(22.dp))

      // 4. Self Timer Options (Interactive!)
      Text(
        text = "Self Timer Delay",
        color = CozyCream,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = "Automatic countdown before shutter release",
        color = CozyMutedText,
        fontSize = 12.sp
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        listOf(0 to "Off", 3 to "3 Seconds", 10 to "10 Seconds").forEach { (sec, label) ->
          val isSelected = settings.timerSeconds == sec
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CozyAmberGold else CozyCharcoalSurface)
              .border(
                width = 1.dp,
                color = if (isSelected) CozyAmberGold else CozyBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
              )
              .clickable { onSetTimer(sec) }
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              color = if (isSelected) CozyObsidian else CozyCream,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.testTag("timer_$sec")
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // 5. Flash Mode Selection
      Text(
        text = "Flash & Illumination",
        color = CozyCream,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = "Hardware strobe, torch, or screen selfie fill",
        color = CozyMutedText,
        fontSize = 12.sp
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("off" to "Off", "auto" to "Auto", "on" to "On", "torch" to "Torch").forEach { (mode, label) ->
          val isSelected = settings.flashMode == mode
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CozyCream else CozyCharcoalSurface)
              .border(
                width = 1.dp,
                color = if (isSelected) CozyCream else CozyBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
              )
              .clickable { onSetFlashMode(mode) }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              color = if (isSelected) CozyObsidian else CozyCream,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // 6. Camera Facing Selector
      Text(
        text = "Active Lens & Facing",
        color = CozyCream,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        listOf(false to "Rear 35mm Lens", true to "Front Selfie Cam").forEach { (isFront, label) ->
          val isSelected = settings.isFrontCamera == isFront
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CozyAmberGold else CozyCharcoalSurface)
              .border(
                width = 1.dp,
                color = if (isSelected) CozyAmberGold else CozyBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
              )
              .clickable {
                if (settings.isFrontCamera != isFront) {
                  onToggleCameraFacing()
                }
              }
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = null,
                tint = if (isSelected) CozyObsidian else CozyCream,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = label,
                color = if (isSelected) CozyObsidian else CozyCream,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // 7. Aspect Ratio Selection
      Text(
        text = "Frame Aspect Ratio",
        color = CozyCream,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("3:4" to "3:4 Classic", "1:1" to "1:1 Square", "16:9" to "16:9 Cinema", "9:16" to "9:16 Reel").forEach { (ratio, label) ->
          val isSelected = settings.aspectRatio == ratio
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CozyCream else CozyCharcoalSurface)
              .border(
                width = 1.dp,
                color = if (isSelected) CozyCream else CozyBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
              )
              .clickable { onSetAspectRatio(ratio) }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = ratio,
              color = if (isSelected) CozyObsidian else CozyCream,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // 8. Export Quality
      Text(
        text = "Export Processing Resolution",
        color = CozyCream,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        listOf("12MP" to "High (12MP)", "24MP" to "Ultra (24MP)").forEach { (resKey, label) ->
          val isSelected = settings.resolution == resKey
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CozyAmberGold else CozyCharcoalSurface)
              .border(
                width = 1.dp,
                color = if (isSelected) CozyAmberGold else CozyBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
              )
              .clickable { onSetResolution(resKey) }
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              color = if (isSelected) CozyObsidian else CozyCream,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 9. Video Recording Settings Shortcut
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(CozyCharcoalSurface)
          .border(1.dp, CozyBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
          .clickable {
            onDismiss()
            onOpenVideoSettings()
          }
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFFE05A47).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Videocam,
              contentDescription = null,
              tint = Color(0xFFE05A47),
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Custom Video FPS & Quality",
              color = CozyCream,
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "${settings.videoQuality} • ${settings.videoFps} FPS",
              color = CozyAmberGold,
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
        Text(
          text = "Configure →",
          color = CozyAmberGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Permissions & Storage Access Section
      Text(
        text = "Permissions & Auto-Saving",
        color = CozyCream,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CozyCharcoalSurface)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Auto-Save to Gallery",
            color = CozyCream,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Always Active",
            color = CozyAmberGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Text(
          text = "Photos are automatically saved to Pictures/CozyCam. Videos are automatically saved to Movies/CozyCam.",
          color = CozyMutedText,
          fontSize = 12.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "System Notifications",
            color = CozyCream,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Enabled",
            color = CozyAmberGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

@Composable
private fun SettingToggleRow(
  title: String,
  description: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onCheckedChange(!checked) }
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        color = CozyCream,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = description,
        color = CozyMutedText,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = CozyObsidian,
        checkedTrackColor = CozyAmberGold,
        uncheckedThumbColor = CozyMutedText,
        uncheckedTrackColor = CozyCharcoalSurface
      )
    )
  }
}
