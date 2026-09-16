package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.ui.theme.CozyAmberGold
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyCharcoalElevated
import com.example.ui.theme.CozyCharcoalSurface
import com.example.ui.theme.CozyCream
import com.example.ui.theme.CozyMutedText
import com.example.ui.theme.CozyObsidian

@Composable
fun CameraToneControlBar(
  exposure: Float,
  temperature: Float,
  onExposureChange: (Float) -> Unit,
  onTemperatureChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Compact pill toggle
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xCC1E1B18))
        .border(1.dp, if (isExpanded) CozyAmberGold else CozyBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        .clickable { isExpanded = !isExpanded }
        .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = "Tone & Exposure",
          tint = if (isExpanded || exposure != 0f || temperature != 0f) CozyAmberGold else CozyCream,
          modifier = Modifier.size(15.dp)
        )

        val evText = if (exposure >= 0) "+%.1f EV".format(exposure) else "%.1f EV".format(exposure)
        val tempText = if (temperature > 0.05f) {
          "Warm +%.1f".format(temperature)
        } else if (temperature < -0.05f) {
          "Cool %.1f".format(temperature)
        } else {
          "Neutral"
        }

        Text(
          text = "$evText • $tempText",
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.SemiBold,
          color = if (exposure != 0f || temperature != 0f) CozyAmberGold else CozyCream
        )
      }
    }

    // Expandable slider panel
    AnimatedVisibility(
      visible = isExpanded,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth(0.94f)
          .padding(top = 8.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(Color(0xEE1E1C1A))
          .border(1.dp, CozyBorder.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        // Exposure Slider Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.WbSunny,
              contentDescription = "Exposure",
              tint = CozyAmberGold,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Exposure (EV)",
              fontSize = 12.sp,
              color = CozyCream,
              fontWeight = FontWeight.Medium
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (exposure >= 0) "+%.1f".format(exposure) else "%.1f".format(exposure),
              fontSize = 12.sp,
              color = CozyAmberGold,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Reset EV
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(CozyCharcoalSurface)
                .clickable { onExposureChange(0f) }
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("0.0", fontSize = 10.sp, color = CozyMutedText, fontFamily = FontFamily.Monospace)
            }
          }
        }

        Slider(
          value = exposure,
          onValueChange = onExposureChange,
          valueRange = -2.0f..2.0f,
          steps = 39,
          colors = SliderDefaults.colors(
            thumbColor = CozyAmberGold,
            activeTrackColor = CozyAmberGold,
            inactiveTrackColor = CozyCharcoalSurface
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Temperature Slider Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Thermostat,
              contentDescription = "Temperature",
              tint = if (temperature >= 0) Color(0xFFFF9E43) else Color(0xFF64B5F6),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Temperature (K)",
              fontSize = 12.sp,
              color = CozyCream,
              fontWeight = FontWeight.Medium
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            val label = if (temperature > 0.05f) "Warm" else if (temperature < -0.05f) "Cool" else "Neutral"
            Text(
              text = "$label (%+.1f)".format(temperature),
              fontSize = 12.sp,
              color = if (temperature >= 0) Color(0xFFFF9E43) else Color(0xFF64B5F6),
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Reset Temp
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(CozyCharcoalSurface)
                .clickable { onTemperatureChange(0f) }
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("0.0", fontSize = 10.sp, color = CozyMutedText, fontFamily = FontFamily.Monospace)
            }
          }
        }

        Slider(
          value = temperature,
          onValueChange = onTemperatureChange,
          valueRange = -1.0f..1.0f,
          steps = 19,
          colors = SliderDefaults.colors(
            thumbColor = if (temperature >= 0) Color(0xFFFF9E43) else Color(0xFF64B5F6),
            activeTrackColor = if (temperature >= 0) Color(0xFFFF9E43) else Color(0xFF64B5F6),
            inactiveTrackColor = CozyCharcoalSurface
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
        )
      }
    }
  }
}
