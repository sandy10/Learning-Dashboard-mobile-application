package com.learning.dashboardmobileapp.core.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Stitch Project: "Offline Learning Dashboard" (6578388657816652202) design tokens
val PrimaryIndigo = Color(0xFF3525CD)
val PrimaryIndigoVariant = Color(0xFF4F46E5)
val PrimaryLight = Color(0xFFE2DFFF)

val SecondaryTeal = Color(0xFF00687A)
val SecondaryTealLight = Color(0xFF57DFFE)
val OnSecondaryContainer = Color(0xFF001F26)

val TertiarySuccess = Color(0xFF00702F)
val TertiarySuccessLight = Color(0xFFE6F8EB)
val TertiarySuccessText = Color(0xFF0F6B2F)

val NeutralPendingBg = Color(0xFFF1F3F5)
val NeutralPendingText = Color(0xFF495057)

val AppBackground = Color(0xFFF7F9FB)
val CardBackground = Color(0xFFFFFFFF)
val SurfaceContainer = Color(0xFFECEEF0)
val SurfaceDim = Color(0xFFD8DADC)
val SurfaceLightGray = Color(0xFFE9ECEF)
val SurfaceSubtle = Color(0xFFF8F9FA)

val TextPrimary = Color(0xFF191C1E)
val TextSecondary = Color(0xFF464555)
val TextTertiary = Color(0xFF777587)
val TextMuted = Color(0xFF6C757D)

val OutlineBorder = Color(0xFFE2E4E8)
val BorderLight = Color(0xFFD0D5DD)
val BorderSubtle = Color(0xFFCED4DA)
val TextFieldBorder = Color(0xFFDEE2E6)

val ErrorRed = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)
val ErrorLightBg = Color(0xFFFFF0F0)
val ErrorLightBorder = Color(0xFFFFCDD2)

val WarningYellowBg = Color(0xFFFFF3CD)
val WarningYellowText = Color(0xFF856404)
val WarningYellowBorder = Color(0xFFFFEEBA)

val DangerLightBg = Color(0xFFFFE5E5)
val CategoryPillBg = Color(0xFFEDE9FE)
val IconGrayDisabled = Color(0xFFADB5BD)

object AppColors {
    val PrimaryIndigo = com.learning.dashboardmobileapp.core.ui.theme.PrimaryIndigo
    val PrimaryIndigoVariant = com.learning.dashboardmobileapp.core.ui.theme.PrimaryIndigoVariant
    val PrimaryLight = com.learning.dashboardmobileapp.core.ui.theme.PrimaryLight
    val SecondaryTeal = com.learning.dashboardmobileapp.core.ui.theme.SecondaryTeal
    val SecondaryTealLight = com.learning.dashboardmobileapp.core.ui.theme.SecondaryTealLight
    val OnSecondaryContainer = com.learning.dashboardmobileapp.core.ui.theme.OnSecondaryContainer
    val TertiarySuccess = com.learning.dashboardmobileapp.core.ui.theme.TertiarySuccess
    val TertiarySuccessLight = com.learning.dashboardmobileapp.core.ui.theme.TertiarySuccessLight
    val TertiarySuccessText = com.learning.dashboardmobileapp.core.ui.theme.TertiarySuccessText
    val NeutralPendingBg = com.learning.dashboardmobileapp.core.ui.theme.NeutralPendingBg
    val NeutralPendingText = com.learning.dashboardmobileapp.core.ui.theme.NeutralPendingText
    val AppBackground = com.learning.dashboardmobileapp.core.ui.theme.AppBackground
    val CardBackground = com.learning.dashboardmobileapp.core.ui.theme.CardBackground
    val SurfaceContainer = com.learning.dashboardmobileapp.core.ui.theme.SurfaceContainer
    val SurfaceDim = com.learning.dashboardmobileapp.core.ui.theme.SurfaceDim
    val SurfaceLightGray = com.learning.dashboardmobileapp.core.ui.theme.SurfaceLightGray
    val SurfaceSubtle = com.learning.dashboardmobileapp.core.ui.theme.SurfaceSubtle
    val TextPrimary = com.learning.dashboardmobileapp.core.ui.theme.TextPrimary
    val TextSecondary = com.learning.dashboardmobileapp.core.ui.theme.TextSecondary
    val TextTertiary = com.learning.dashboardmobileapp.core.ui.theme.TextTertiary
    val TextMuted = com.learning.dashboardmobileapp.core.ui.theme.TextMuted
    val OutlineBorder = com.learning.dashboardmobileapp.core.ui.theme.OutlineBorder
    val BorderLight = com.learning.dashboardmobileapp.core.ui.theme.BorderLight
    val BorderSubtle = com.learning.dashboardmobileapp.core.ui.theme.BorderSubtle
    val TextFieldBorder = com.learning.dashboardmobileapp.core.ui.theme.TextFieldBorder
    val ErrorRed = com.learning.dashboardmobileapp.core.ui.theme.ErrorRed
    val ErrorContainer = com.learning.dashboardmobileapp.core.ui.theme.ErrorContainer
    val ErrorLightBg = com.learning.dashboardmobileapp.core.ui.theme.ErrorLightBg
    val ErrorLightBorder = com.learning.dashboardmobileapp.core.ui.theme.ErrorLightBorder
    val WarningYellowBg = com.learning.dashboardmobileapp.core.ui.theme.WarningYellowBg
    val WarningYellowText = com.learning.dashboardmobileapp.core.ui.theme.WarningYellowText
    val WarningYellowBorder = com.learning.dashboardmobileapp.core.ui.theme.WarningYellowBorder
    val DangerLightBg = com.learning.dashboardmobileapp.core.ui.theme.DangerLightBg
    val CategoryPillBg = com.learning.dashboardmobileapp.core.ui.theme.CategoryPillBg
    val IconGrayDisabled = com.learning.dashboardmobileapp.core.ui.theme.IconGrayDisabled
}

val LearningColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryIndigoVariant,
    onPrimaryContainer = Color.White,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = SecondaryTealLight,
    onSecondaryContainer = OnSecondaryContainer,
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
