package com.btech_dev.quotebro.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Quote: ImageVector
    get() {
        if (_Quote != null) {
            return _Quote!!
        }
        _Quote = ImageVector.Builder(
            name = "Quote",
            defaultWidth = 1024.dp,
            defaultHeight = 1024.dp,
            viewportWidth = 1024f,
            viewportHeight = 1024f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(256f, 554.7f)
                curveToRelative(-119.5f, 0f, -213.3f, -93.9f, -213.3f, -213.3f)
                reflectiveCurveToRelative(93.9f, -213.3f, 213.3f, -213.3f)
                reflectiveCurveToRelative(213.3f, 93.9f, 213.3f, 213.3f)
                reflectiveCurveTo(375.5f, 554.7f, 256f, 554.7f)
                close()
                moveTo(256f, 213.3f)
                curveTo(183.5f, 213.3f, 128f, 268.8f, 128f, 341.3f)
                reflectiveCurveToRelative(55.5f, 128f, 128f, 128f)
                reflectiveCurveToRelative(128f, -55.5f, 128f, -128f)
                reflectiveCurveTo(328.5f, 213.3f, 256f, 213.3f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(128f, 896f)
                curveToRelative(-25.6f, 0f, -42.7f, -17.1f, -42.7f, -42.7f)
                reflectiveCurveToRelative(17.1f, -42.7f, 42.7f, -42.7f)
                curveTo(379.7f, 810.7f, 384f, 345.6f, 384f, 341.3f)
                curveToRelative(0f, -25.6f, 17.1f, -42.7f, 42.7f, -42.7f)
                reflectiveCurveToRelative(42.7f, 17.1f, 42.7f, 42.7f)
                curveTo(469.3f, 362.7f, 465.1f, 896f, 128f, 896f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(768f, 554.7f)
                curveToRelative(-119.5f, 0f, -213.3f, -93.9f, -213.3f, -213.3f)
                reflectiveCurveToRelative(93.9f, -213.3f, 213.3f, -213.3f)
                reflectiveCurveToRelative(213.3f, 93.9f, 213.3f, 213.3f)
                reflectiveCurveTo(887.5f, 554.7f, 768f, 554.7f)
                close()
                moveTo(768f, 213.3f)
                curveToRelative(-72.5f, 0f, -128f, 55.5f, -128f, 128f)
                reflectiveCurveToRelative(55.5f, 128f, 128f, 128f)
                reflectiveCurveToRelative(128f, -55.5f, 128f, -128f)
                reflectiveCurveTo(840.5f, 213.3f, 768f, 213.3f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(640f, 896f)
                curveToRelative(-25.6f, 0f, -42.7f, -17.1f, -42.7f, -42.7f)
                reflectiveCurveToRelative(17.1f, -42.7f, 42.7f, -42.7f)
                curveToRelative(251.7f, 0f, 256f, -465.1f, 256f, -469.3f)
                curveToRelative(0f, -25.6f, 17.1f, -42.7f, 42.7f, -42.7f)
                reflectiveCurveToRelative(42.7f, 17.1f, 42.7f, 42.7f)
                curveTo(981.3f, 362.7f, 977.1f, 896f, 640f, 896f)
                close()
            }
        }.build()

        return _Quote!!
    }

@Suppress("ObjectPropertyName")
private var _Quote: ImageVector? = null
