package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EduPrimaryLight,
    onPrimary = Color(0xFF00227B),
    primaryContainer = EduPrimaryDark,
    onPrimaryContainer = EduPrimaryContainer,
    secondary = EduSecondaryLight,
    onSecondary = Color(0xFF003822),
    secondaryContainer = EduSecondaryDark,
    onSecondaryContainer = EduSecondaryContainer,
    tertiary = EduTertiaryLight,
    onTertiary = Color(0xFF452200),
    background = DarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EduPrimary,
    onPrimary = Color.White,
    primaryContainer = EduPrimaryContainer,
    onPrimaryContainer = EduOnPrimaryContainer,
    secondary = EduSecondary,
    onSecondary = Color.White,
    secondaryContainer = EduSecondaryContainer,
    onSecondaryContainer = EduOnSecondaryContainer,
    tertiary = EduTertiary,
    onTertiary = Color.White,
    tertiaryContainer = EduTertiaryContainer,
    onTertiaryContainer = EduOnTertiaryContainer,
    background = LightBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = LightOutline
  )

@Composable
fun EduCardTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set false by default to showcase Edu Card branded theme
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
