package cn.super12138.todo.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GitHubIcon: ImageVector
    get() {
        if (_GitHubIcon != null) {
            return _GitHubIcon!!
        }
        _GitHubIcon = ImageVector.Builder(
            name = "GitHubIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12.5f, 0.75f)
                curveTo(6.146f, 0.75f, 1f, 5.896f, 1f, 12.25f)
                curveToRelative(0f, 5.089f, 3.292f, 9.387f, 7.863f, 10.91f)
                curveToRelative(0.575f, 0.101f, 0.79f, -0.244f, 0.79f, -0.546f)
                curveToRelative(0f, -0.273f, -0.014f, -1.178f, -0.014f, -2.142f)
                curveToRelative(-2.889f, 0.532f, -3.636f, -0.704f, -3.866f, -1.35f)
                curveToRelative(-0.13f, -0.331f, -0.69f, -1.352f, -1.18f, -1.625f)
                curveToRelative(-0.402f, -0.216f, -0.977f, -0.748f, -0.014f, -0.762f)
                curveToRelative(0.906f, -0.014f, 1.553f, 0.834f, 1.769f, 1.179f)
                curveToRelative(1.035f, 1.74f, 2.688f, 1.25f, 3.349f, 0.948f)
                curveToRelative(0.1f, -0.747f, 0.402f, -1.25f, 0.733f, -1.538f)
                curveToRelative(-2.559f, -0.287f, -5.232f, -1.279f, -5.232f, -5.678f)
                curveToRelative(0f, -1.25f, 0.445f, -2.285f, 1.178f, -3.09f)
                curveToRelative(-0.115f, -0.288f, -0.517f, -1.467f, 0.115f, -3.048f)
                curveToRelative(0f, 0f, 0.963f, -0.302f, 3.163f, 1.179f)
                curveToRelative(0.92f, -0.259f, 1.897f, -0.388f, 2.875f, -0.388f)
                curveToRelative(0.977f, 0f, 1.955f, 0.13f, 2.875f, 0.388f)
                curveToRelative(2.2f, -1.495f, 3.162f, -1.179f, 3.162f, -1.179f)
                curveToRelative(0.633f, 1.581f, 0.23f, 2.76f, 0.115f, 3.048f)
                curveToRelative(0.733f, 0.805f, 1.179f, 1.825f, 1.179f, 3.09f)
                curveToRelative(0f, 4.413f, -2.688f, 5.39f, -5.247f, 5.678f)
                curveToRelative(0.417f, 0.36f, 0.776f, 1.05f, 0.776f, 2.128f)
                curveToRelative(0f, 1.538f, -0.014f, 2.774f, -0.014f, 3.162f)
                curveToRelative(0f, 0.302f, 0.216f, 0.662f, 0.79f, 0.547f)
                curveTo(20.709f, 21.637f, 24f, 17.324f, 24f, 12.25f)
                curveTo(24f, 5.896f, 18.854f, 0.75f, 12.5f, 0.75f)
                close()
            }
        }.build()

        return _GitHubIcon!!
    }

@Suppress("ObjectPropertyName")
private var _GitHubIcon: ImageVector? = null
