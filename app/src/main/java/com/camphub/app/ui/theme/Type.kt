package com.camphub.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.camphub.app.R

// Headings
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

// Body and labels
val SourceSans3 = FontFamily(
    Font(R.font.source_sans_3, FontWeight.Normal),
    Font(R.font.source_sans_3_semibold, FontWeight.SemiBold)
)

// Material 3 defaults; only font, weight, size, and line height are changed
private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium = base.displayMedium.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = base.displaySmall.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = base.headlineLarge.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = base.headlineMedium.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = base.headlineSmall.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = base.titleLarge.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = base.titleMedium.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = base.titleSmall.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    // "tnum" keeps digits the same width so prices and dates line up
    bodyLarge = base.bodyLarge.copy(fontFamily = SourceSans3, fontWeight = FontWeight.Normal, fontSize = 17.sp, lineHeight = 26.sp, fontFeatureSettings = "tnum"),
    bodyMedium = base.bodyMedium.copy(fontFamily = SourceSans3, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp, fontFeatureSettings = "tnum"),
    bodySmall = base.bodySmall.copy(fontFamily = SourceSans3, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, fontFeatureSettings = "tnum"),
    labelLarge = base.labelLarge.copy(fontFamily = SourceSans3, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = base.labelMedium.copy(fontFamily = SourceSans3, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 16.sp),
    labelSmall = base.labelSmall.copy(fontFamily = SourceSans3, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp)
)