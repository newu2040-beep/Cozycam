package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.room.PhotoEntity
import com.example.domain.models.CameraIllustrationType
import com.example.ui.components.VintageCameraIllustration
import com.example.ui.theme.CozyCharcoal
import com.example.ui.theme.CozyCharcoalElevated
import com.example.ui.theme.CozyCharcoalSurface
import com.example.ui.theme.CozyCream
import com.example.ui.theme.CozyMutedText
import com.example.ui.theme.CozyObsidian
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GalleryScreen(
  photos: List<PhotoEntity>,
  onPhotoClick: (PhotoEntity) -> Unit,
  onToggleFavorite: (Long, Boolean) -> Unit,
  onDeletePhoto: (Long) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf("All") }
  val tabs = listOf("All", "Photos", "Favorites")

  val filteredPhotos = remember(photos, selectedTab) {
    when (selectedTab) {
      "Favorites" -> photos.filter { it.isFavorite }
      else -> photos
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CozyObsidian)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
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
            .testTag("gallery_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = CozyCream
          )
        }

        Text(
          text = "Film Roll",
          color = CozyCream,
          fontSize = 18.sp,
          fontWeight = FontWeight.SemiBold
        )

        Text(
          text = "${photos.size} shots",
          color = CozyMutedText,
          fontSize = 13.sp,
          fontWeight = FontWeight.Normal,
          modifier = Modifier.padding(end = 8.dp)
        )
      }

      // Filter Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        tabs.forEach { tab ->
          val isSelected = tab == selectedTab
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(18.dp))
              .background(if (isSelected) CozyCream else CozyCharcoalSurface)
              .clickable { selectedTab = tab }
              .padding(horizontal = 16.dp, vertical = 8.dp)
          ) {
            Text(
              text = tab,
              color = if (isSelected) CozyObsidian else CozyMutedText,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      if (filteredPhotos.isEmpty()) {
        // Empty State
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          VintageCameraIllustration(
            type = CameraIllustrationType.RANGEFINDER_35MM,
            size = 110.dp
          )
          Spacer(modifier = Modifier.height(20.dp))
          Text(
            text = "Your film roll is empty",
            color = CozyCream,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Take vintage photos to build your collection.",
            color = CozyMutedText,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
          )
        }
      } else {
        // 2-Column Photo Grid
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 28.dp, top = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(filteredPhotos, key = { it.id }) { photo ->
            PhotoGridItem(
              photo = photo,
              onClick = { onPhotoClick(photo) },
              onToggleFavorite = { onToggleFavorite(photo.id, photo.isFavorite) },
              onDelete = { onDeletePhoto(photo.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PhotoGridItem(
  photo: PhotoEntity,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val file = File(photo.uri)
  val bitmap = remember(photo.uri) {
    if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
  }

  val dateStr = remember(photo.createdDate) {
    val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.US)
    sdf.format(Date(photo.createdDate))
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(0.75f)
      .clip(RoundedCornerShape(18.dp))
      .background(CozyCharcoalElevated)
      .clickable(onClick = onClick)
  ) {
    if (bitmap != null) {
      Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Photo",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    } else {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(CozyCharcoal),
        contentAlignment = Alignment.Center
      ) {
        Text("Photo", color = CozyMutedText, fontSize = 12.sp)
      }
    }

    // Top action pills
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0x80000000))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = photo.presetName.ifBlank { "Classic" },
          color = CozyCream,
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color(0x80000000))
            .clickable(onClick = onToggleFavorite),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (photo.isFavorite) Color(0xFFFF453A) else CozyCream,
            modifier = Modifier.size(14.dp)
          )
        }

        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color(0x80000000))
            .clickable(onClick = onDelete),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete",
            tint = CozyCream,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }

    // Bottom date stamp bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .background(Color(0x66000000))
        .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
      Text(
        text = dateStr,
        color = CozyMutedText,
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal
      )
    }
  }
}
