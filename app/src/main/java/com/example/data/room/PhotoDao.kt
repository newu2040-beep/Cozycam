package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
  @Query("SELECT * FROM photos ORDER BY createdDate DESC")
  fun getAllPhotos(): Flow<List<PhotoEntity>>

  @Query("SELECT * FROM photos WHERE isFavorite = 1 ORDER BY createdDate DESC")
  fun getFavoritePhotos(): Flow<List<PhotoEntity>>

  @Query("SELECT * FROM photos WHERE presetId = :presetId ORDER BY createdDate DESC")
  fun getPhotosByPreset(presetId: String): Flow<List<PhotoEntity>>

  @Query("SELECT * FROM photos WHERE id = :id LIMIT 1")
  suspend fun getPhotoById(id: Long): PhotoEntity?

  @Query("SELECT * FROM photos ORDER BY createdDate DESC LIMIT 1")
  fun getLatestPhoto(): Flow<PhotoEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPhoto(photo: PhotoEntity): Long

  @Update
  suspend fun updatePhoto(photo: PhotoEntity)

  @Query("UPDATE photos SET isFavorite = :isFavorite WHERE id = :id")
  suspend fun updateFavorite(id: Long, isFavorite: Boolean)

  @Query("DELETE FROM photos WHERE id = :id")
  suspend fun deletePhotoById(id: Long)
}

@Dao
interface PresetDao {
  @Query("SELECT * FROM presets")
  fun getAllPresets(): Flow<List<PresetEntity>>

  @Query("SELECT * FROM presets WHERE isFavorite = 1")
  fun getFavoritePresets(): Flow<List<PresetEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPresets(presets: List<PresetEntity>)

  @Query("UPDATE presets SET isFavorite = :isFavorite WHERE id = :id")
  suspend fun updateFavorite(id: String, isFavorite: Boolean)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistory(history: EditHistoryEntity): Long

  @Query("SELECT * FROM edit_history WHERE photoId = :photoId ORDER BY createdDate DESC")
  fun getHistoryForPhoto(photoId: Long): Flow<List<EditHistoryEntity>>
}
