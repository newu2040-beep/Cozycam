package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CozyDarkColorScheme = darkColorScheme(
  primary = CozyCream,
  onPrimary = CozyObsidian,
  primaryContainer = CozyCharcoalElevated,
  onPrimaryContainer = CozyCream,
  secondary = CozyAmberGold,
  onSecondary = CozyObsidian,
  secondaryContainer = CozyCharcoalSurface,
  onSecondaryContainer = CozyAmberGold,
  tertiary = CozyFilmOrange,
  onTertiary = Color.White,
  background = CozyObsidian,
  onBackground = CozyCream,
  surface = CozyCharcoal,
  onSurface = CozyCream,
  surfaceVariant = CozyCharcoalSurface,
  onSurfaceVariant = CozyMutedText,
  outline = CozyBorder,
  outlineVariant = CozyBorder.copy(alpha = 0.5f)
)

private val CozyLightColorScheme = lightColorScheme(
  primary = CozyObsidian,
  onPrimary = CozyCream,
  primaryContainer = CozyCreamSubtle,
  onPrimaryContainer = CozyObsidian,
  secondary = CozyAmberGold,
  onSecondary = Color.White,
  background = CozyCream,
  onBackground = CozyObsidian,
  surface = Color.White,
  onSurface = CozyObsidian,
  surfaceVariant = CozyCreamSubtle,
  onSurfaceVariant = CozyCharcoal,
  outline = CozyBorder
)

@Composable
fun CozyCamTheme(
  darkTheme: Boolean = true, // Dark-first aesthetic as requested in PRD
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) CozyDarkColorScheme else CozyLightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Kept for backward compatibility if referenced
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  CozyCamTheme(darkTheme = darkTheme, content = content)
}
