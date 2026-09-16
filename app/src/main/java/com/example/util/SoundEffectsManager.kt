package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaActionSound
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build

object SoundEffectsManager {
  private var mediaActionSound: MediaActionSound? = null
  private var toneGenerator: ToneGenerator? = null

  fun init() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        mediaActionSound = MediaActionSound()
        mediaActionSound?.load(MediaActionSound.SHUTTER_CLICK)
        mediaActionSound?.load(MediaActionSound.START_VIDEO_RECORDING)
        mediaActionSound?.load(MediaActionSound.STOP_VIDEO_RECORDING)
      }
    } catch (_: Throwable) {
      // Hardware sound loading safety
    }
  }

  private fun getToneGenerator(): ToneGenerator? {
    if (toneGenerator == null) {
      try {
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
      } catch (_: Throwable) {
        // Fallback for emulators with disabled tone generator HAL
      }
    }
    return toneGenerator
  }

  fun playShutterSound(soundStyle: String, enabled: Boolean = true) {
    if (!enabled || soundStyle.equals("Mute", ignoreCase = true)) return

    try {
      when (soundStyle.lowercase()) {
        "mechanical slr", "classic slr", "classic" -> {
          mediaActionSound?.play(MediaActionSound.SHUTTER_CLICK)
        }
        "retro film advance", "film advance" -> {
          mediaActionSound?.play(MediaActionSound.SHUTTER_CLICK)
          getToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
        }
        "twin-lens snap", "vintage snap" -> {
          mediaActionSound?.play(MediaActionSound.SHUTTER_CLICK)
        }
        "digital beep", "beep" -> {
          getToneGenerator()?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
        }
        else -> {
          mediaActionSound?.play(MediaActionSound.SHUTTER_CLICK)
        }
      }
    } catch (_: Throwable) {
      // Fail-safe silent catch
    }
  }

  fun playVideoRecordSound(isStarting: Boolean, enabled: Boolean = true) {
    if (!enabled) return
    try {
      if (isStarting) {
        mediaActionSound?.play(MediaActionSound.START_VIDEO_RECORDING)
      } else {
        mediaActionSound?.play(MediaActionSound.STOP_VIDEO_RECORDING)
      }
    } catch (_: Throwable) {
      try {
        getToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 100)
      } catch (_: Throwable) {}
    }
  }

  fun release() {
    try {
      mediaActionSound?.release()
      mediaActionSound = null
      toneGenerator?.release()
      toneGenerator = null
    } catch (_: Throwable) {}
  }
}
