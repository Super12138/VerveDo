package cn.super12138.todo.ui

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

object VerveDoDefaults {
    /**
     * 屏幕左右两边预留边距（防止内容全部贴边显示过丑）
     */
    val screenHorizontalPadding = 16.dp

    /**
     * 屏幕上下预留边距（防止内容全部贴边显示过丑）
     */
    val screenVerticalPadding = 8.dp

    /**
     * 待办卡片默认高度
     */
    val toDoCardHeight = 86.dp

    /**
     * 设置项水平边距
     */
    val settingsItemHorizontalPadding = 24.dp

    /**
     * 设置项垂直边距
     */
    val settingsItemVerticalPadding = 16.dp

    val settingsItemPadding = 4.dp

    val overviewCardHeight = 120.dp

    val ScreenContainerShape: Shape
        @Composable
        get() = MaterialTheme.shapes.large/*.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = ZeroCornerSize
        )*/

    object Colors {
        val Container: Color
            @Composable
            get() = MaterialTheme.colorScheme.surfaceBright

        val Background: Color
            @Composable
            get() = MaterialTheme.colorScheme.surfaceContainer

        val Green = Color(0xFF349938)
    }

    val fadedEdgeWidth = 8.dp

    val defaultShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.large

    val pressedShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.small


    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun shapes() = ButtonShapes(
        shape = defaultShape,
        pressedShape = pressedShape
    )

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    val shapesDefaultAnimationSpec: FiniteAnimationSpec<Float>
        @Composable
        get() = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
}