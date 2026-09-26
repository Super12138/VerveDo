package cn.super12138.todo.ui

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char

object VerveDoDefaults {
    val contentPadding = 8.dp

    /**
     * 屏幕左右两边预留边距（防止内容全部贴边显示过丑）
     */
    val screenHorizontalPadding = 16.dp

    /**
     * 屏幕上下预留边距（防止内容全部贴边显示过丑）
     */
    val screenVerticalPadding = 8.dp

    /**
     * 设置项水平边距
     */
    val settingsItemHorizontalPadding = 24.dp

    /**
     * 设置项垂直边距
     */
    val settingsItemVerticalPadding = 16.dp

    val settingsItemPadding = 4.dp

    object Colors {
        val Container: Color
            @Composable get() = MaterialTheme.colorScheme.surfaceBright
        val Background: Color
            @Composable get() = MaterialTheme.colorScheme.surfaceContainer
        val Green = Color(0xFF349938)
    }

    object Sizes {
        val taskCardHeight = 86.dp
        val overviewCardHeight = 120.dp
        val fadedEdgeWidth = 8.dp

        object EmptyTip {
            val default = 48.dp
            val large = 96.dp
        }
    }

    val ScreenContainerShape: Shape
        @Composable get() = MaterialTheme.shapes.large/*.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = ZeroCornerSize
        )*/

    val defaultShape: CornerBasedShape
        @Composable get() = MaterialTheme.shapes.large

    val pressedShape: CornerBasedShape
        @Composable get() = MaterialTheme.shapes.small

    val shapes: ButtonShapes
        @Composable get() = ButtonDefaults.shapes(
            shape = defaultShape,
            pressedShape = pressedShape
        )

    val listColor: CardColors
        @Composable get() = CardDefaults.cardColors(
            containerColor = Colors.Container
        )

    val shapesDefaultAnimationSpec: FiniteAnimationSpec<Float>
        @Composable get() = MaterialTheme.motionScheme.defaultEffectsSpec()

    val toggleButtonColors: ToggleButtonColors
        @Composable get() = ToggleButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)

    val defaultDateFormatter = LocalDateTime.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        day()
    }

    val fullDateFormatter = LocalDateTime.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        day()
        char('-')
        hour()
        char('-')
        minute()
        char('-')
        second()
    }
}