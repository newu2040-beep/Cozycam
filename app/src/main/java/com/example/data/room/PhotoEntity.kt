package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val uri: String,
  val createdDate: Long = System.currentTimeMillis(),
  val presetId: String,
  val presetName: String = "",
  val isFavorite: Boolean = false,
  val width: Int = 0,
  val height: Int = 0,
  val camera: String = "rear",
  val metadataJson: String = "{}"
)
