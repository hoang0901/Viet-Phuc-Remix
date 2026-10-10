package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = VermilionRed,
    onPrimary = PureWhite,
    primaryContainer = CardHighlight,
    onPrimaryContainer = VermilionDark,
    secondary = JadeTeal,
    onSecondary = PureWhite,
    secondaryContainer = JadeTealLight.copy(alpha = 0.25f),
    onSecondaryContainer = JadeTeal,
    tertiary = ImperialGold,
    onTertiary = PureWhite,
    tertiaryContainer = ImperialGoldLight.copy(alpha = 0.3f),
    onTertiaryContainer = ImperialGold,
    background = IvoryBase,
    onBackground = CharcoalPrimary,
    surface = PureWhite,
    onSurface = CharcoalPrimary,
    surfaceVariant = CreamSurface,
    onSurfaceVariant = CharcoalSecondary,
    outline = BorderCream,
    outlineVariant = BorderCream.copy(alpha = 0.6f)
)

private val DarkColorScheme = darkColorScheme(
    primary = VermilionRed,
    onPrimary = PureWhite,
    primaryContainer = VermilionDark,
    onPrimaryContainer = PureWhite,
    secondary = JadeTealLight,
    onSecondary = CharcoalPrimary,
    secondaryContainer = JadeTeal,
    onSecondaryContainer = PureWhite,
    tertiary = ImperialGoldLight,
    onTertiary = CharcoalPrimary,
    background = CharcoalPrimary,
    onBackground = IvoryBase,
    surface = Color(0xFF282D2A),
    onSurface = IvoryBase,
    surfaceVariant = Color(0xFF353C37),
    onSurfaceVariant = BorderCream,
    outline = CharcoalMuted
)

@Composable
fun VietPhucRemixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
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
