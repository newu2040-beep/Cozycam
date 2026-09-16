package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.FilmPreset
import com.example.domain.models.FilmPresetRepository
import com.example.ui.components.VintageCameraIllustration
import com.example.ui.theme.CozyAmberGold
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyCharcoal
import com.example.ui.theme.CozyCharcoalElevated
import com.example.ui.theme.CozyCharcoalSurface
import com.example.ui.theme.CozyCream
import com.example.ui.theme.CozyMutedText
import com.example.ui.theme.CozyObsidian

@Composable
fun PresetsScreen(
  activePreset: FilmPreset,
  favoritePresetIds: Set<String>,
  onSelectPreset: (FilmPreset) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("All") }
  val categories = listOf("All", "Film", "Digital", "Instant", "Disposable", "Video", "Creative", "B&W")

  val filteredPresets = remember(selectedCategory, favoritePresetIds) {
    FilmPresetRepository.allPresets.filter {
      if (selectedCategory == "All") true
      else it.category.equals(selectedCategory, ignoreCase = true)
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CozyObsidian)
      .statusBarsPadding()
      .navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 720.dp)
    ) {
      // Top Navigation Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(44.dp)
            .testTag("presets_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = CozyCream
          )
        }

        Text(
          text = "Film Presets",
          color = CozyCream,
          fontSize = 18.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.size(44.dp))
      }

      // Categories Horizontal Scroll Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { category ->
          val isSelected = category == selectedCategory
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) CozyCream else CozyCharcoalSurface)
              .clickable { selectedCategory = category }
              .padding(horizontal = 16.dp, vertical = 9.dp)
          ) {
            Text(
              text = category,
              color = if (isSelected) CozyObsidian else CozyMutedText,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Adaptive Preset Cards Grid
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp, top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredPresets, key = { it.id }) { preset ->
          val isSelected = preset.id == activePreset.id
          val isFav = favoritePresetIds.contains(preset.id)

          PresetCard(
            preset = preset,
            isSelected = isSelected,
            isFavorite = isFav,
            onSelect = { onSelectPreset(preset) },
            onToggleFavorite = { onToggleFavorite(preset.id) }
          )
        }
      }
    }
  }
}

@Composable
private fun PresetCard(
  preset: FilmPreset,
  isSelected: Boolean,
  isFavorite: Boolean,
  onSelect: () -> Unit,
  onToggleFavorite: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderModifier = if (isSelected) {
    Modifier.border(2.dp, CozyAmberGold, RoundedCornerShape(22.dp))
  } else {
    Modifier.border(1.dp, CozyBorder.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .then(borderModifier)
      .background(
        Brush.verticalGradient(
          colors = listOf(
            CozyCharcoalElevated,
            CozyCharcoal
          )
        )
      )
      .clickable(onClick = onSelect)
      .padding(14.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Favorite button row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0x33000000))
            .clickable(onClick = onToggleFavorite),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) Color(0xFFFF453A) else CozyMutedText,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Camera Illustration
      VintageCameraIllustration(
        type = preset.cameraType,
        size = 88.dp,
        modifier = Modifier.padding(vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Preset Name
      Text(
        text = preset.name,
        color = CozyCream,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.3.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(3.dp))

      // Subtitle / Tone tags
      Text(
        text = preset.subtitle,
        color = CozyMutedText,
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
