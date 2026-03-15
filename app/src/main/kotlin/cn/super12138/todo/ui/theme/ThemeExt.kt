package cn.super12138.todo.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.utils.blend
import com.kyant.m3color.hct.Hct
import com.kyant.m3color.scheme.SchemeContent
import com.kyant.m3color.scheme.SchemeExpressive
import com.kyant.m3color.scheme.SchemeFidelity
import com.kyant.m3color.scheme.SchemeFruitSalad
import com.kyant.m3color.scheme.SchemeMonochrome
import com.kyant.m3color.scheme.SchemeNeutral
import com.kyant.m3color.scheme.SchemeRainbow
import com.kyant.m3color.scheme.SchemeTonalSpot
import com.kyant.m3color.scheme.SchemeVibrant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Stable
fun dynamicColorScheme(
    keyColor: Color,
    isDark: Boolean,
    pureBlack: Boolean,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
    animationSpec: AnimationSpec<Color> = MaterialTheme.motionScheme.defaultEffectsSpec()
): ColorScheme {
    /**
     * 应用纯黑深色模式
     * * 启用条件：深色模式+纯黑深色模式均为启用
     */
    fun Color.applyPureBlack(fraction: Float = 0.5f): Color =
        if (isDark && pureBlack) this.darken(fraction) else this

    fun Color.replaceByPureBlack(color: Color): Color =
        if (isDark && pureBlack) color else this

    val hct = Hct.fromInt(keyColor.toArgb())
    val scheme = when (style) {
        PaletteStyle.TonalSpot -> SchemeTonalSpot(hct, isDark, contrastLevel)
        PaletteStyle.Neutral -> SchemeNeutral(hct, isDark, contrastLevel)
        PaletteStyle.Vibrant -> SchemeVibrant(hct, isDark, contrastLevel)
        PaletteStyle.Expressive -> SchemeExpressive(hct, isDark, contrastLevel)
        PaletteStyle.Rainbow -> SchemeRainbow(hct, isDark, contrastLevel)
        PaletteStyle.FruitSalad -> SchemeFruitSalad(hct, isDark, contrastLevel)
        PaletteStyle.Monochrome -> SchemeMonochrome(hct, isDark, contrastLevel)
        PaletteStyle.Fidelity -> SchemeFidelity(hct, isDark, contrastLevel)
        PaletteStyle.Content -> SchemeContent(hct, isDark, contrastLevel)
    }

    return ColorScheme(
        primary = scheme.primary
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        onPrimary = scheme.onPrimary
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        primaryContainer = scheme.primaryContainer
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        onPrimaryContainer = scheme.onPrimaryContainer
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        inversePrimary = scheme.inversePrimary
            .toColor()
            .applyPureBlack(0.1f)
            .animate(animationSpec),
        secondary = scheme.secondary
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        onSecondary = scheme.onSecondary
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        secondaryContainer = scheme.secondaryContainer
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        onSecondaryContainer = scheme.onSecondaryContainer
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        tertiary = scheme.tertiary
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        onTertiary = scheme.onTertiary
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        tertiaryContainer = scheme.tertiaryContainer
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        onTertiaryContainer = scheme.onTertiaryContainer
            .toColor()
            .applyPureBlack(0.2f)
            .animate(animationSpec),
        background = scheme.background
            .toColor()
            .replaceByPureBlack(Color.Black)
            .animate(animationSpec),
        onBackground = scheme.onBackground
            .toColor()
            .applyPureBlack(0.15f)
            .animate(animationSpec),
        surface = scheme.surface
            .toColor()
            .replaceByPureBlack(Color.Black)
            .animate(animationSpec),
        onSurface = scheme.onSurface
            .toColor()
            .applyPureBlack(0.15f)
            .animate(animationSpec),
        surfaceVariant = scheme.surfaceVariant
            .toColor()
            .animate(animationSpec),
        onSurfaceVariant = scheme.onSurfaceVariant
            .toColor()
            .animate(animationSpec),
        surfaceTint = scheme.surfaceTint
            .toColor()
            .animate(animationSpec),
        inverseSurface = scheme.inverseSurface
            .toColor()
            .applyPureBlack(0.5f)
            .animate(animationSpec),
        inverseOnSurface = scheme.inverseOnSurface
            .toColor()
            .applyPureBlack(0.1f)
            .animate(animationSpec),
        error = scheme.error
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        onError = scheme.onError
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        errorContainer = scheme.errorContainer
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        onErrorContainer = scheme.onErrorContainer
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        outline = scheme.outline
            .toColor()
            .applyPureBlack(0.2f)
            .animate(animationSpec),
        outlineVariant = scheme.outlineVariant
            .toColor()
            .applyPureBlack(0.2f)
            .animate(animationSpec),
        scrim = scheme.scrim
            .toColor()
            .animate(animationSpec),
        surfaceBright = scheme.surfaceBright
            .toColor()
            .applyPureBlack(0.3f)
            .animate(animationSpec),
        surfaceDim = scheme.surfaceDim
            .toColor()
            .applyPureBlack(0.2f)
            .animate(animationSpec),
        surfaceContainer = scheme.surfaceContainer
            .toColor()
            .replaceByPureBlack(Color.Black)
            .animate(animationSpec),
        surfaceContainerHigh = scheme.surfaceContainerHigh
            .toColor()
            .applyPureBlack(0.2f)
            .animate(animationSpec),
        surfaceContainerHighest = scheme.surfaceContainerHighest
            .toColor()
            .applyPureBlack(0.2f)
            .animate(animationSpec),
        surfaceContainerLow = scheme.surfaceContainerLow
            .toColor()
            .applyPureBlack(0.2f)
            .animate(animationSpec),
        surfaceContainerLowest = scheme.surfaceContainerLowest
            .toColor()
            .applyPureBlack(0.2f)
            .animate(animationSpec),
        primaryFixed = scheme.primaryFixed.toColor().animate(animationSpec),
        primaryFixedDim = scheme.primaryFixedDim.toColor().animate(animationSpec),
        onPrimaryFixed = scheme.onPrimaryFixed.toColor().animate(animationSpec),
        onPrimaryFixedVariant = scheme.onPrimaryFixedVariant.toColor().animate(animationSpec),
        secondaryFixed = scheme.secondaryFixed.toColor().animate(animationSpec),
        secondaryFixedDim = scheme.secondaryFixedDim.toColor().animate(animationSpec),
        onSecondaryFixed = scheme.onSecondaryFixed.toColor().animate(animationSpec),
        onSecondaryFixedVariant = scheme.onSecondaryFixedVariant.toColor().animate(animationSpec),
        tertiaryFixed = scheme.tertiaryFixed.toColor().animate(animationSpec),
        tertiaryFixedDim = scheme.tertiaryFixedDim.toColor().animate(animationSpec),
        onTertiaryFixed = scheme.onTertiaryFixed.toColor().animate(animationSpec),
        onTertiaryFixedVariant = scheme.onTertiaryFixedVariant.toColor().animate(animationSpec),
    )
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Int.toColor(): Color = Color(this)

// https://github.com/jordond/MaterialKolor/blob/main/material-kolor/src/commonMain/kotlin/com/materialkolor/DynamicMaterialTheme.kt
@Composable
private fun Color.animate(animationSpec: AnimationSpec<Color> = spring()): Color =
    animateColorAsState(this, animationSpec).value

// https://github.com/hushenghao/AndroidEasterEggs/blob/main/core/theme/src/main/java/com/dede/android_eggs/views/theme/Theme.kt#L21
/**
 * 让颜色变暗
 */
private fun Color.darken(fraction: Float = 0.5f): Color =
    Color(this.toArgb().blend(Color.Black.toArgb(), fraction))

private fun Color.replace(color: Color): Color = color
private fun Color.replace(color: Int): Color = color.toColor()