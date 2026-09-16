package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [PhotoEntity::class, PresetEntity::class, EditHistoryEntity::class],
  version = 1,
  exportSchema = false
)
abstract class CozyCamDatabase : RoomDatabase() {
  abstract fun photoDao(): PhotoDao
  abstract fun presetDao(): PresetDao

  companion object {
    @Volatile
    private var INSTANCE: CozyCamDatabase? = null

    fun getDatabase(context: Context): CozyCamDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CozyCamDatabase::class.java,
          "cozycam_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
