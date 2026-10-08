package com.learning.dashboardmobileapp.core.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Stitch Project: "Offline Learning Dashboard" (6578388657816652202) design tokens
val PrimaryIndigo = Color(0xFF3525CD)
val PrimaryIndigoVariant = Color(0xFF4F46E5)
val PrimaryLight = Color(0xFFE2DFFF)

val SecondaryTeal = Color(0xFF00687A)
val SecondaryTealLight = Color(0xFF57DFFE)

val TertiarySuccess = Color(0xFF00702F)
val TertiarySuccessLight = Color(0xFFE6F8EB)
val TertiarySuccessText = Color(0xFF0F6B2F)

val NeutralPendingBg = Color(0xFFF1F3F5)
val NeutralPendingText = Color(0xFF495057)

val AppBackground = Color(0xFFF7F9FB)
val CardBackground = Color(0xFFFFFFFF)
val SurfaceContainer = Color(0xFFECEEF0)
val SurfaceDim = Color(0xFFD8DADC)

val TextPrimary = Color(0xFF191C1E)
val TextSecondary = Color(0xFF464555)
val TextTertiary = Color(0xFF777587)
val OutlineBorder = Color(0xFFE2E4E8)

val ErrorRed = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)

val LearningColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryIndigoVariant,
    onPrimaryContainer = Color.White,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = SecondaryTealLight,
    onSecondaryContainer = Color(0xFF001F26),
    tertiary = TertiarySuccess,
    onTertiary = Color.White,
    tertiaryContainer = TertiarySuccessLight,
    onTertiaryContainer = TertiarySuccessText,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = TextSecondary,
    outline = OutlineBorder,
    error = ErrorRed,
    errorContainer = ErrorContainer
)
