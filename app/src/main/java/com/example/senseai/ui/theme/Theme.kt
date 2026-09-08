package com.example.senseai.ui.theme

import android.app.Activity
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

private val SenseAIDarkColorScheme =
    darkColorScheme(
        primary = PrimaryBlue,
        onPrimary = Color.Black,

        secondary = SecondaryGreen,
        onSecondary = Color.Black,

        tertiary = InfoBlue,
        onTertiary = Color.Black,

        background = DarkBackground,
        onBackground = TextPrimary,

        surface = DarkSurface,
        onSurface = TextPrimary,

        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = TextSecondary,

        error = DangerRed,
        onError = Color.White
    )

private val SenseAILightColorScheme =
    lightColorScheme(
        primary = Color(0xFF0066CC),
        onPrimary = Color.White,

        secondary = Color(0xFF087F23),
        onSecondary = Color.White,

        tertiary = Color(0xFF006B8F),
        onTertiary = Color.White,

        background = Color(0xFFF7F9FC),
        onBackground = Color(0xFF101418),

        surface = Color.White,
        onSurface = Color(0xFF101418),

        surfaceVariant = Color(0xFFE8EEF5),
        onSurfaceVariant = Color(0xFF45515C),

        error = Color(0xFFBA1A1A),
        onError = Color.White
    )

@Composable
fun SenseAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme =
        when {
            dynamicColor &&
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.S -> {
                if (darkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
            }

            darkTheme -> {
                SenseAIDarkColorScheme
            }

            else -> {
                SenseAILightColorScheme
            }
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}