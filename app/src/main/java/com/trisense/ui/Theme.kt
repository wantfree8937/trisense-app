package com.trisense.ui

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = OnPrimary,
    onBackground = OnBackground,
    onSurface = OnSurface,
    error = ErrorColor
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    // Keep default light scheme simple or match requested premium feel if light mode is used,
    // but app spec suggests a focused/lab feel, dark often preferred.
    // For now, leaving standard light scheme or could force dark.
)

@Composable
fun TriSenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), 
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to enforce our premium palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Force Dark Theme for now as "Lab" feel usually implies it, or use Light if system is light.
        // Actually, let's respect system setting but default to Dark scheme if simpler.
        // Let's stick to system setting.
        // else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
