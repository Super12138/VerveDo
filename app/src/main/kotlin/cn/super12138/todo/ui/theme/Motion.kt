package cn.super12138.todo.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.unveilIn
import androidx.compose.animation.veilOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 来自：https://github.com/Ashinch/ReadYou/blob/main/app/src/main/java/me/ash/reader/ui/motion/MaterialSharedAxis.kt
 */

private const val ProgressThreshold = 0.35f
private const val DEFAULT_START_SCALE = 0.95f //0.92f
private const val THRESHOLD_ALPHA = 0.5f

private val Int.ForOutgoing: Int
    get() = (this * ProgressThreshold).toInt()

private val Int.ForIncoming: Int
    get() = this - this.ForOutgoing


private const val DefaultMotionDuration: Int = 300

/**
 * [materialSharedAxisX] allows to switch a layout with shared X-axis transition.
 *
 */
fun materialSharedAxisX(
    initialOffsetX: (fullWidth: Int) -> Int,
    targetOffsetX: (fullWidth: Int) -> Int,
    durationMillis: Int = DefaultMotionDuration,
): ContentTransform = ContentTransform(
    materialSharedAxisXIn(
        initialOffsetX = initialOffsetX,
        durationMillis = durationMillis
    ), materialSharedAxisXOut(
        targetOffsetX = targetOffsetX,
        durationMillis = durationMillis
    )
)

/**
 * [materialSharedAxisXIn] allows to switch a layout with shared X-axis enter transition.
 */
fun materialSharedAxisXIn(
    initialOffsetX: (fullWidth: Int) -> Int,
    durationMillis: Int = DefaultMotionDuration,
): EnterTransition = slideInHorizontally(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = FastOutSlowInEasing
    ),
    initialOffsetX = initialOffsetX
) + fadeIn(
    animationSpec = tween(
        durationMillis = durationMillis.ForIncoming,
        delayMillis = durationMillis.ForOutgoing,
        easing = LinearOutSlowInEasing
    )
)

/**
 * [materialSharedAxisXOut] allows to switch a layout with shared X-axis exit transition.
 *
 */
fun materialSharedAxisXOut(
    targetOffsetX: (fullWidth: Int) -> Int,
    durationMillis: Int = DefaultMotionDuration,
): ExitTransition = slideOutHorizontally(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = FastOutSlowInEasing
    ),
    targetOffsetX = targetOffsetX
) + fadeOut(
    animationSpec = tween(
        durationMillis = durationMillis.ForOutgoing,
        delayMillis = 0,
        easing = FastOutLinearInEasing
    )
)

/*fun fadeThrough(
    durationMillis: Int = 200,
): ContentTransform = ContentTransform(
    fadeThroughIn(durationMillis = durationMillis),
    fadeThroughOut(durationMillis = durationMillis)
)

fun fadeThroughIn(
    durationMillis: Int = DefaultMotionDuration,
): EnterTransition =
    fadeIn(
        animationSpec = tween(durationMillis),
        initialAlpha = THRESHOLD_ALPHA
    ) + scaleIn(
        animationSpec = tween(durationMillis),
        initialScale = DEFAULT_START_SCALE
    )

fun fadeThroughOut(
    durationMillis: Int = DefaultMotionDuration,
): ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = FastOutLinearInEasing
    )
)*/

fun veilFade(
    initialColor: Color,
    durationMillis: Int = 200
): ContentTransform = ContentTransform(
    veilFadeIn(
        initialColor = initialColor,
        durationMillis = durationMillis
    ),
    veilFadeOut(
        initialColor = initialColor,
        durationMillis = durationMillis
    )
)

@OptIn(ExperimentalAnimationApi::class)
fun veilFadeIn(
    initialColor: Color,
    durationMillis: Int = DefaultMotionDuration,
): EnterTransition =
    fadeIn(
    ) + unveilIn(
        initialColor = initialColor
    )

@OptIn(ExperimentalAnimationApi::class)
fun veilFadeOut(
    initialColor: Color,
    durationMillis: Int = DefaultMotionDuration,
): ExitTransition =
    fadeOut() + veilOut(
        targetColor = initialColor
    )

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun fadeScaleIn(
    effectSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.fastEffectsSpec(),
    spatialSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.fastSpatialSpec()
): EnterTransition = fadeIn(effectSpec) +
        scaleIn(
            animationSpec = spatialSpec,
            initialScale = 0.92f
        )

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun fadeScaleOut(
    effectSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.fastEffectsSpec()
): ExitTransition = fadeOut(effectSpec)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun fadeScale(
    effectSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.fastEffectsSpec(),
    spatialSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.fastSpatialSpec()
): ContentTransform = ContentTransform(
    targetContentEnter = fadeScaleIn(
        effectSpec = effectSpec,
        spatialSpec = spatialSpec
    ),
    initialContentExit = fadeScaleOut(effectSpec = effectSpec)
)
