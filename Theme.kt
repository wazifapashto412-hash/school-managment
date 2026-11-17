package com.schoolmanagement.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(primary = Color(0xFF0D47A1), secondary = Color(0xFF00897B), tertiary = Color(0xFFF9A825))
private val Dark = darkColorScheme(primary = Color(0xFF90CAF9), secondary = Color(0xFF80CBC4), tertiary = Color(0xFFFFD54F))

@Composable
fun SchoolTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
