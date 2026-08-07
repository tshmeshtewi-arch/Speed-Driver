package com.turboclone.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TurboDarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    secondary = RedLight,
    onSecondary = White,
    background = BlackBg,
    onBackground = White,
    surface = SurfaceDark,
    onSurface = White,
    surfaceVariant = SurfaceDark2,
    error = RedLight
)

@Composable
fun TurboCloneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // التطبيق مصمم بالوضع الداكن دائمًا كسمة أساسية (مطلوب في المواصفات)
    MaterialTheme(
        colorScheme = TurboDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
