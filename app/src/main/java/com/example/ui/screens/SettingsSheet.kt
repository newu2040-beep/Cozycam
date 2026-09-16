package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.CameraSettings
import com.example.ui.theme.CozyAmberGold
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyCharcoal
import com.example.ui.theme.CozyCharcoalSurface
import com.example.ui.theme.CozyCream
import com.example.ui.theme.CozyMutedText
import com.example.ui.theme.CozyObsidian

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
  settings: CameraSettings,
  onDismiss: () -> Unit,
  onToggleGrid: () -> Unit,
  onSetTimer: (Int) -> Unit,
  onToggleHaptics: () -> Unit,
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
        .padding(bottom = 32.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Camera Preferences",
          color = CozyCream,
          fontSize = 18.sp,
          fontWeight = FontWeight.SemiBold
        )
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

      // Rule of Thirds Grid
      SettingToggleRow(
        title = "Rule of Thirds Grid",
        description = "Align photos with photographic grid guides",
        checked = settings.gridEnabled,
        onCheckedChange = { onToggleGrid() }
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Haptic Feedback
      SettingToggleRow(
        title = "Haptic Shutter",
        description = "Tactile mechanical vibration on press",
        checked = settings.hapticsEnabled,
        onCheckedChange = { onToggleHaptics() }
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Date Stamp on Negatives
      SettingToggleRow(
        title = "Vintage Date Stamp",
        description = "Print retro amber timestamp on bottom corner",
        checked = settings.dateStampEnabled,
        onCheckedChange = {}
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Self Timer Options
      Text(
        text = "Self Timer",
        color = CozyCream,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        listOf(0 to "Off", 3 to "3s", 10 to "10s").forEach { (sec, label) ->
          val isSelected = settings.timerSeconds == sec
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CozyCream else CozyCharcoalSurface)
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              color = if (isSelected) CozyObsidian else CozyCream,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
              modifier = Modifier.testTag("timer_$sec")
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Export Quality
      Text(
        text = "Export Processing",
        color = CozyCream,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        listOf("High (12MP)", "Ultra (24MP)").forEach { qual ->
          val isSelected = qual.startsWith(settings.resolution.take(2))
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CozyAmberGold else CozyCharcoalSurface)
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = qual,
              color = if (isSelected) CozyObsidian else CozyCream,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
          }
        }
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
    modifier = Modifier.fillMaxWidth(),
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
        checkedTrackColor = CozyCream,
        uncheckedThumbColor = CozyMutedText,
        uncheckedTrackColor = CozyCharcoalSurface
      )
    )
  }
}
