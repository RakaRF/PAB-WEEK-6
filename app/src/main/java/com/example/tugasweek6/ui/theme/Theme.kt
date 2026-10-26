package com.example.tugasweek6.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Tema gelap bergaya game: navy + emas + biru diamond.
private val GameColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = OnGold,
    secondary = DiamondBlue,
    onSecondary = OnDiamondBlue,
    tertiary = ArcaneViolet,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavyVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = NavyContainer,
    outline = NavyOutline,
)

@Composable
fun TugasWeek6Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content
    )
}
