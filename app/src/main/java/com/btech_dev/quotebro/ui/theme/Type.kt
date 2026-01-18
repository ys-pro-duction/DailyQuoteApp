package com.btech_dev.quotebro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppFont = FontFamily.Default

fun AppTypography(fontScale: Float = 1.0f): Typography {
    return Typography(
        headlineLarge = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Bold,
            fontSize = (32 * fontScale).sp,
            lineHeight = (40 * fontScale).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Bold,
            fontSize = (24 * fontScale).sp,
            lineHeight = (32 * fontScale).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Bold,
            fontSize = (22 * fontScale).sp,
            lineHeight = (28 * fontScale).sp
        ),
        titleLarge = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Bold,
            fontSize = (20 * fontScale).sp
        ),
        titleMedium = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16 * fontScale).sp
        ),
        titleSmall = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Bold,
            fontSize = (14 * fontScale).sp
        ),
        bodyLarge = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * fontScale).sp,
            lineHeight = (24 * fontScale).sp
        ),
        bodyMedium = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * fontScale).sp,
            lineHeight = (20 * fontScale).sp
        ),
        bodySmall = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * fontScale).sp,
            lineHeight = (16 * fontScale).sp
        ),
        labelLarge = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * fontScale).sp
        ),
        labelMedium = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * fontScale).sp
        ),
        labelSmall = TextStyle(
            fontFamily = AppFont,
            fontWeight = FontWeight.Bold,
            fontSize = (10 * fontScale).sp
        )
    )
}