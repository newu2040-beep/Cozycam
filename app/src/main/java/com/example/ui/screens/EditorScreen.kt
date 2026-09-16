package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.FilmProcessingParams
import com.example.ui.theme.CozyAmberGold
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyCharcoal
import com.example.ui.theme.CozyCharcoalElevated
import com.example.ui.theme.CozyCharcoalSurface
import com.example.ui.theme.CozyCream
import com.example.ui.theme.CozyMutedText
import com.example.ui.theme.CozyObsidian

enum class EditorTool {
  NONE,
  ADJUST,
  GRAIN,
  LIGHT_LEAK,
  DATE,
  VIGNETTE,
  BLOOM,
  DUST,
  FRAME
}

@Composable
fun EditorScreen(
  processedBitmap: Bitmap?,
  params: FilmProcessingParams,
  saveMessage: String?,
  isProcessing: Boolean,
  onUpdateParams: (FilmProcessingParams) -> Unit,
  onSave: () -> Unit,
  onShare: () -> Unit,
  onDelete: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var activeTool by remember { mutableStateOf(EditorTool.NONE) }
  var adjustSubTab by remember { mutableStateOf("Exposure") }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CozyObsidian)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Top Navigation Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(44.dp)
            .testTag("editor_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = CozyCream
          )
        }

        Text(
          text = "Photo Result",
          color = CozyCream,
          fontSize = 17.sp,
          fontWeight = FontWeight.Medium
        )

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(44.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "Options",
            tint = CozyCream
          )
        }
      }

      // 2. Cinematic Preview Area
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(CozyCharcoal),
        contentAlignment = Alignment.Center
      ) {
        if (processedBitmap != null) {
          Image(
            bitmap = processedBitmap.asImageBitmap(),
            contentDescription = "Processed Photo",
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Fit
          )
        } else {
          CircularProgressIndicator(
            color = CozyCream,
            modifier = Modifier.size(36.dp)
          )
        }

        if (isProcessing) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0x66000000)),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(color = CozyAmberGold)
          }
        }
      }

      // 3. Tool adjustment slider (when a tool is selected)
      AnimatedVisibility(
        visible = activeTool != EditorTool.NONE,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        ToolAdjustmentPanel(
          activeTool = activeTool,
          params = params,
          adjustSubTab = adjustSubTab,
          onSubTabChange = { adjustSubTab = it },
          onParamsChange = onUpdateParams,
          onCloseTool = { activeTool = EditorTool.NONE }
        )
      }

      // 4. Editing Tools Row (Adjust, Grain, Light Leak, Date, Vignette, Bloom, Dust)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        EditorToolItem(
          icon = Icons.Default.Tune,
          label = "Adjust",
          isSelected = activeTool == EditorTool.ADJUST,
          onClick = { activeTool = if (activeTool == EditorTool.ADJUST) EditorTool.NONE else EditorTool.ADJUST }
        )
        EditorToolItem(
          icon = Icons.Default.Grain,
          label = "Grain",
          isSelected = activeTool == EditorTool.GRAIN,
          onClick = { activeTool = if (activeTool == EditorTool.GRAIN) EditorTool.NONE else EditorTool.GRAIN }
        )
        EditorToolItem(
          icon = Icons.Default.WbSunny,
          label = "Light Leak",
          isSelected = activeTool == EditorTool.LIGHT_LEAK,
          onClick = { activeTool = if (activeTool == EditorTool.LIGHT_LEAK) EditorTool.NONE else EditorTool.LIGHT_LEAK }
        )
        EditorToolItem(
          icon = Icons.Default.DateRange,
          label = "Date",
          isSelected = activeTool == EditorTool.DATE,
          onClick = { activeTool = if (activeTool == EditorTool.DATE) EditorTool.NONE else EditorTool.DATE }
        )
        EditorToolItem(
          icon = Icons.Default.AutoAwesome,
          label = "Vignette",
          isSelected = activeTool == EditorTool.VIGNETTE,
          onClick = { activeTool = if (activeTool == EditorTool.VIGNETTE) EditorTool.NONE else EditorTool.VIGNETTE }
        )
        EditorToolItem(
          icon = Icons.Default.AutoAwesome,
          label = "Bloom",
          isSelected = activeTool == EditorTool.BLOOM,
          onClick = { activeTool = if (activeTool == EditorTool.BLOOM) EditorTool.NONE else EditorTool.BLOOM }
        )
        EditorToolItem(
          icon = Icons.Default.Grain,
          label = "Dust",
          isSelected = activeTool == EditorTool.DUST,
          onClick = { activeTool = if (activeTool == EditorTool.DUST) EditorTool.NONE else EditorTool.DUST }
        )
        EditorToolItem(
          icon = Icons.Default.Tune,
          label = "Frame",
          isSelected = activeTool == EditorTool.FRAME,
          onClick = { activeTool = if (activeTool == EditorTool.FRAME) EditorTool.NONE else EditorTool.FRAME }
        )
      }

      // 5. Bottom Action Pill Buttons (Save, Share, More)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Save pill
        Box(
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(CozyCharcoalElevated)
            .clickable(onClick = onSave)
            .testTag("save_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Save",
              tint = CozyCream,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Save",
              color = CozyCream,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Share pill
        Box(
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(CozyCharcoalElevated)
            .clickable(onClick = onShare)
            .testTag("share_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              tint = CozyCream,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Share",
              color = CozyCream,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // More / Edit pill (accent cream color, matching reference design!)
        Box(
          modifier = Modifier
            .weight(1.1f)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(CozyCream)
            .clickable { activeTool = EditorTool.ADJUST },
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "More",
              tint = CozyObsidian,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Adjust",
              color = CozyObsidian,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    // Save notification banner
    if (saveMessage != null) {
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(CozyAmberGold)
          .padding(horizontal = 20.dp, vertical = 10.dp)
      ) {
        Text(
          text = saveMessage,
          color = CozyObsidian,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

@Composable
private fun EditorToolItem(
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(if (isSelected) CozyCream else CozyCharcoalSurface),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isSelected) CozyObsidian else CozyCream,
        modifier = Modifier.size(18.dp)
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      color = if (isSelected) CozyCream else CozyMutedText,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
    )
  }
}

@Composable
private fun ToolAdjustmentPanel(
  activeTool: EditorTool,
  params: FilmProcessingParams,
  adjustSubTab: String,
  onSubTabChange: (String) -> Unit,
  onParamsChange: (FilmProcessingParams) -> Unit,
  onCloseTool: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(20.dp))
      .background(CozyCharcoalElevated)
      .padding(16.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      when (activeTool) {
        EditorTool.ADJUST -> {
          val subTabs = listOf("Exposure", "Contrast", "Saturation", "Temp", "Tint")
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            subTabs.forEach { tab ->
              val isSel = tab == adjustSubTab
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(if (isSel) CozyCream else CozyCharcoalSurface)
                  .clickable { onSubTabChange(tab) }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = tab,
                  color = if (isSel) CozyObsidian else CozyMutedText,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          when (adjustSubTab) {
            "Exposure" -> {
              SliderWithLabel(
                label = "Exposure: ${"%.2f".format(params.exposure)}",
                value = params.exposure,
                valueRange = -1.0f..1.0f,
                onValueChange = { onParamsChange(params.copy(exposure = it)) }
              )
            }
            "Contrast" -> {
              SliderWithLabel(
                label = "Contrast: ${"%.2f".format(params.contrast)}",
                value = params.contrast,
                valueRange = 0.5f..1.8f,
                onValueChange = { onParamsChange(params.copy(contrast = it)) }
              )
            }
            "Saturation" -> {
              SliderWithLabel(
                label = "Saturation: ${"%.2f".format(params.saturation)}",
                value = params.saturation,
                valueRange = 0.0f..2.0f,
                onValueChange = { onParamsChange(params.copy(saturation = it)) }
              )
            }
            "Temp" -> {
              SliderWithLabel(
                label = "Temperature: ${"%.2f".format(params.temperature)}",
                value = params.temperature,
                valueRange = -1.0f..1.0f,
                onValueChange = { onParamsChange(params.copy(temperature = it)) }
              )
            }
            "Tint" -> {
              SliderWithLabel(
                label = "Tint: ${"%.2f".format(params.tint)}",
                value = params.tint,
                valueRange = -1.0f..1.0f,
                onValueChange = { onParamsChange(params.copy(tint = it)) }
              )
            }
          }
        }
        EditorTool.GRAIN -> {
          SliderWithLabel(
            label = "Film Grain Intensity: ${"%.2f".format(params.grain)}",
            value = params.grain,
            valueRange = 0.0f..1.0f,
            onValueChange = { onParamsChange(params.copy(grain = it)) }
          )
        }
        EditorTool.LIGHT_LEAK -> {
          SliderWithLabel(
            label = "Light Leak Flare: ${"%.2f".format(params.lightLeak)}",
            value = params.lightLeak,
            valueRange = 0.0f..1.0f,
            onValueChange = { onParamsChange(params.copy(lightLeak = it)) }
          )
        }
        EditorTool.DATE -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Print Retro Date Stamp", color = CozyCream, fontSize = 14.sp)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (params.dateStampEnabled) CozyCream else CozyCharcoalSurface)
                .clickable { onParamsChange(params.copy(dateStampEnabled = !params.dateStampEnabled)) }
                .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
              Text(
                text = if (params.dateStampEnabled) "ON" else "OFF",
                color = if (params.dateStampEnabled) CozyObsidian else CozyMutedText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
        EditorTool.VIGNETTE -> {
          SliderWithLabel(
            label = "Lens Vignette Falloff: ${"%.2f".format(params.vignette)}",
            value = params.vignette,
            valueRange = 0.0f..1.0f,
            onValueChange = { onParamsChange(params.copy(vignette = it)) }
          )
        }
        EditorTool.BLOOM -> {
          SliderWithLabel(
            label = "Halation / Bloom Glow: ${"%.2f".format(params.halation)}",
            value = params.halation,
            valueRange = 0.0f..1.0f,
            onValueChange = { onParamsChange(params.copy(halation = it, bloom = it * 0.8f)) }
          )
        }
        EditorTool.DUST -> {
          SliderWithLabel(
            label = "Analog Dust & Scratches: ${"%.2f".format(params.dust)}",
            value = params.dust,
            valueRange = 0.0f..1.0f,
            onValueChange = { onParamsChange(params.copy(dust = it)) }
          )
        }
        EditorTool.FRAME -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("none" to "Clean", "polaroid" to "Polaroid", "film35mm" to "35mm Film").forEach { (style, name) ->
              val isSel = params.frameStyle == style
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSel) CozyCream else CozyCharcoalSurface)
                  .clickable { onParamsChange(params.copy(frameStyle = style)) }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = name,
                  color = if (isSel) CozyObsidian else CozyCream,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
        else -> {}
      }
    }
  }
}

@Composable
private fun SliderWithLabel(
  label: String,
  value: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  onValueChange: (Float) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      color = CozyCream,
      fontSize = 13.sp,
      fontWeight = FontWeight.Medium
    )
    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = valueRange,
      colors = SliderDefaults.colors(
        thumbColor = CozyCream,
        activeTrackColor = CozyAmberGold,
        inactiveTrackColor = CozyBorder
      )
    )
  }
}
