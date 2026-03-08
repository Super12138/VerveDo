package cn.super12138.todo.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal val ButtonShapes.hasRoundedCornerShapes: Boolean
    get() = shape is RoundedCornerShape && pressedShape is RoundedCornerShape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal val ButtonShapes.hasCornerBasedShapes: Boolean
    get() = shape is CornerBasedShape && pressedShape is CornerBasedShape

/**
 * 来自 Compose 内部类
 *
 * 根据交互状态返回对应的形状
 *
 * * 如果 *原形状* 和 *按下形状* **均为圆角形状**则在形状变换时使用动画过渡
 *
 * @param shapes 按钮形状（需包含原形状和按下形状）
 * @param pressed 是否处于按下状态
 * @param animationSpec 动画曲线
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun shapeByInteraction(
    shapes: ButtonShapes,
    pressed: Boolean,
    animationSpec: FiniteAnimationSpec<Float>,
): Shape {
    val shape =
        if (pressed) {
            shapes.pressedShape
        } else {
            shapes.shape
        }
    if (shapes.hasRoundedCornerShapes)
        return key(shapes) { rememberAnimatedShape(shape as RoundedCornerShape, animationSpec) }
    else if (shapes.hasCornerBasedShapes)
        return key(shapes) { rememberAnimatedShape(shape as CornerBasedShape, animationSpec) }

    return shape
}

@Stable
internal class AnimatedShapeState(
    val shape: RoundedCornerShape,
    val spec: FiniteAnimationSpec<Float>,
) {
    var size: Size = Size.Zero
    var density: Density = Density(0f, 0f)

    private var topStart: Animatable<Float, AnimationVector1D>? = null

    private var topEnd: Animatable<Float, AnimationVector1D>? = null

    private var bottomStart: Animatable<Float, AnimationVector1D>? = null

    private var bottomEnd: Animatable<Float, AnimationVector1D>? = null

    fun topStart(size: Size = this.size, density: Density = this.density): Float {
        return (topStart ?: Animatable(shape.topStart.toPx(size, density)).also { topStart = it })
            .value
    }

    fun topEnd(size: Size = this.size, density: Density = this.density): Float {
        return (topEnd ?: Animatable(shape.topEnd.toPx(size, density)).also { topEnd = it }).value
    }

    fun bottomStart(size: Size = this.size, density: Density = this.density): Float {
        return (bottomStart
            ?: Animatable(shape.bottomStart.toPx(size, density)).also { bottomStart = it })
            .value
    }

    fun bottomEnd(size: Size = this.size, density: Density = this.density): Float {
        return (bottomEnd
            ?: Animatable(shape.bottomEnd.toPx(size, density)).also { bottomEnd = it })
            .value
    }

    suspend fun animateToShape(shape: CornerBasedShape) = coroutineScope {
        launch { topStart?.animateTo(shape.topStart.toPx(size, density), spec) }
        launch { topEnd?.animateTo(shape.topEnd.toPx(size, density), spec) }
        launch { bottomStart?.animateTo(shape.bottomStart.toPx(size, density), spec) }
        launch { bottomEnd?.animateTo(shape.bottomEnd.toPx(size, density), spec) }
    }
}

@Composable
private fun rememberAnimatedShape(state: AnimatedShapeState): Shape {
    val density = LocalDensity.current
    state.density = density

    return remember(density, state) {
        object : ShapeWithHorizontalCenterOptically {
            var clampedRange by mutableStateOf(0f..1f)

            override fun offset(): Float {
                val topStart = state.topStart().coerceIn(clampedRange)
                val topEnd = state.topEnd().coerceIn(clampedRange)
                val bottomStart = state.bottomStart().coerceIn(clampedRange)
                val bottomEnd = state.bottomEnd().coerceIn(clampedRange)
                val avgStart = (topStart + bottomStart) / 2
                val avgEnd = (topEnd + bottomEnd) / 2
                return CenterOpticallyCoefficient * (avgStart - avgEnd)
            }

            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density,
            ): Outline {
                state.size = size

                clampedRange = 0f..size.height / 2
                return state.shape
                    .copy(
                        topStart = CornerSize(state.topStart().coerceIn(clampedRange)),
                        topEnd = CornerSize(state.topEnd().coerceIn(clampedRange)),
                        bottomStart = CornerSize(state.bottomStart().coerceIn(clampedRange)),
                        bottomEnd = CornerSize(state.bottomEnd().coerceIn(clampedRange)),
                    )
                    .createOutline(size, layoutDirection, density)
            }
        }
    }
}

@Composable
internal fun rememberAnimatedShape(
    currentShape: RoundedCornerShape,
    animationSpec: FiniteAnimationSpec<Float>,
): Shape {
    val state =
        remember(animationSpec) { AnimatedShapeState(shape = currentShape, spec = animationSpec) }

    val channel = remember { Channel<RoundedCornerShape>(Channel.CONFLATED) }

    SideEffect { channel.trySend(currentShape) }
    LaunchedEffect(state, channel) {
        for (target in channel) {
            val newTarget = channel.tryReceive().getOrNull() ?: target
            launch { state.animateToShape(newTarget) }
        }
    }

    return rememberAnimatedShape(state)
}

@Stable
internal class AnimatedCornerBasedShapeState(
    val shape: CornerBasedShape,
    val spec: FiniteAnimationSpec<Float>,
) {
    var size: Size = Size.Zero
    var density: Density = Density(0f, 0f)

    private var topStart: Animatable<Float, AnimationVector1D>? = null

    private var topEnd: Animatable<Float, AnimationVector1D>? = null

    private var bottomStart: Animatable<Float, AnimationVector1D>? = null

    private var bottomEnd: Animatable<Float, AnimationVector1D>? = null

    fun topStart(size: Size = this.size, density: Density = this.density): Float {
        return (topStart ?: Animatable(shape.topStart.toPx(size, density)).also { topStart = it })
            .value
    }

    fun topEnd(size: Size = this.size, density: Density = this.density): Float {
        return (topEnd ?: Animatable(shape.topEnd.toPx(size, density)).also { topEnd = it }).value
    }

    fun bottomStart(size: Size = this.size, density: Density = this.density): Float {
        return (bottomStart
            ?: Animatable(shape.bottomStart.toPx(size, density)).also { bottomStart = it })
            .value
    }

    fun bottomEnd(size: Size = this.size, density: Density = this.density): Float {
        return (bottomEnd
            ?: Animatable(shape.bottomEnd.toPx(size, density)).also { bottomEnd = it })
            .value
    }

    suspend fun animateToShape(shape: CornerBasedShape) = coroutineScope {
        launch { topStart?.animateTo(shape.topStart.toPx(size, density), spec) }
        launch { topEnd?.animateTo(shape.topEnd.toPx(size, density), spec) }
        launch { bottomStart?.animateTo(shape.bottomStart.toPx(size, density), spec) }
        launch { bottomEnd?.animateTo(shape.bottomEnd.toPx(size, density), spec) }
    }
}

@Composable
private fun rememberAnimatedShape(state: AnimatedCornerBasedShapeState): Shape {
    val density = LocalDensity.current
    state.density = density

    return remember(density, state) {
        object : ShapeWithHorizontalCenterOptically {
            var clampedRange by mutableStateOf(0f..1f)

            override fun offset(): Float {
                val topStart = state.topStart().coerceIn(clampedRange)
                val topEnd = state.topEnd().coerceIn(clampedRange)
                val bottomStart = state.bottomStart().coerceIn(clampedRange)
                val bottomEnd = state.bottomEnd().coerceIn(clampedRange)
                val avgStart = (topStart + bottomStart) / 2
                val avgEnd = (topEnd + bottomEnd) / 2
                return CenterOpticallyCoefficient * (avgStart - avgEnd)
            }

            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density,
            ): Outline {
                state.size = size

                clampedRange = 0f..size.height / 2
                return RoundedCornerShape(
                    topStart = state.topStart().coerceIn(clampedRange),
                    topEnd = state.topEnd().coerceIn(clampedRange),
                    bottomStart = state.bottomStart().coerceIn(clampedRange),
                    bottomEnd = state.bottomEnd().coerceIn(clampedRange),
                )
                    .createOutline(size, layoutDirection, density)
            }
        }
    }
}

@Composable
internal fun rememberAnimatedShape(
    currentShape: CornerBasedShape,
    animationSpec: FiniteAnimationSpec<Float>,
): Shape {
    val state =
        remember(animationSpec) {
            AnimatedCornerBasedShapeState(shape = currentShape, spec = animationSpec)
        }

    val channel = remember { Channel<CornerBasedShape>(Channel.CONFLATED) }

    SideEffect { channel.trySend(currentShape) }
    LaunchedEffect(state, channel) {
        for (target in channel) {
            val newTarget = channel.tryReceive().getOrNull() ?: target
            launch { state.animateToShape(newTarget) }
        }
    }

    return rememberAnimatedShape(state)
}

internal interface ShapeWithHorizontalCenterOptically : Shape {
    fun offset(): Float
}

internal const val CenterOpticallyCoefficient = 0.11f
