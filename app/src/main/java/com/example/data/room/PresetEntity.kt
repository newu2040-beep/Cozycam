package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets")
data class PresetEntity(
  @PrimaryKey val id: String,
  val name: String,
  val category: String,
  val description: String = "",
  val parametersJson: String = "",
  val isFavorite: Boolean = false,
  val isEnabled: Boolean = true
)

@Entity(tableName = "edit_history")
data class EditHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val photoId: Long,
  val presetId: String,
  val editParametersJson: String,
  val createdDate: Long = System.currentTimeMillis()
)
