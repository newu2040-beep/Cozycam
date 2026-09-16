package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cozycam_settings")

class UserPreferencesRepository(private val context: Context) {
  companion object {
    val KEY_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    val KEY_DEFAULT_PRESET = stringPreferencesKey("default_preset")
    val KEY_GRID_ENABLED = booleanPreferencesKey("grid_enabled")
    val KEY_FLASH_MODE = stringPreferencesKey("flash_mode")
    val KEY_TIMER_SECONDS = intPreferencesKey("timer_seconds")
    val KEY_ASPECT_RATIO = stringPreferencesKey("aspect_ratio")
    val KEY_HAPTICS = booleanPreferencesKey("haptics_enabled")
    val KEY_SHUTTER_SOUND = booleanPreferencesKey("shutter_sound")
    val KEY_DATE_STAMP = booleanPreferencesKey("date_stamp")
    val KEY_EXPORT_QUALITY = stringPreferencesKey("export_quality")
  }

  val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_FIRST_LAUNCH] ?: true
  }

  val defaultPreset: Flow<String> = context.dataStore.data.map { prefs ->
    prefs[KEY_DEFAULT_PRESET] ?: "classic_35mm"
  }

  val gridEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_GRID_ENABLED] ?: true
  }

  val hapticsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_HAPTICS] ?: true
  }

  val dateStampEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_DATE_STAMP] ?: true
  }

  suspend fun setFirstLaunchCompleted() {
    context.dataStore.edit { it[KEY_FIRST_LAUNCH] = false }
  }

  suspend fun setDefaultPreset(presetId: String) {
    context.dataStore.edit { it[KEY_DEFAULT_PRESET] = presetId }
  }

  suspend fun setGridEnabled(enabled: Boolean) {
    context.dataStore.edit { it[KEY_GRID_ENABLED] = enabled }
  }

  suspend fun setHapticsEnabled(enabled: Boolean) {
    context.dataStore.edit { it[KEY_HAPTICS] = enabled }
  }

  suspend fun setDateStampEnabled(enabled: Boolean) {
    context.dataStore.edit { it[KEY_DATE_STAMP] = enabled }
  }

  suspend fun setFlashMode(mode: String) {
    context.dataStore.edit { it[KEY_FLASH_MODE] = mode }
  }

  suspend fun setTimerSeconds(seconds: Int) {
    context.dataStore.edit { it[KEY_TIMER_SECONDS] = seconds }
  }

  suspend fun setAspectRatio(ratio: String) {
    context.dataStore.edit { it[KEY_ASPECT_RATIO] = ratio }
  }

  suspend fun setExportQuality(quality: String) {
    context.dataStore.edit { it[KEY_EXPORT_QUALITY] = quality }
  }
}
