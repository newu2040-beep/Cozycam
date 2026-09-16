package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.CozyCamApp
import com.example.ui.theme.CozyCamTheme
import com.example.ui.theme.CozyObsidian
import com.example.util.SoundEffectsManager

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    SoundEffectsManager.init()
    enableEdgeToEdge()
    setContent {
      CozyCamTheme(darkTheme = true) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = CozyObsidian
        ) {
          CozyCamApp()
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    SoundEffectsManager.release()
  }
}
