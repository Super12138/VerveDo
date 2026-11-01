package cn.super12138.todo.ui.theme

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.utils.darken
import cn.super12138.todo.utils.toColor
import com.kyant.m3color.dynamiccolor.ColorSpec
import com.kyant.m3color.dynamiccolor.DynamicScheme
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

@Stable
fun dynamicColorScheme(
    keyColor: Color,
    isDark: Boolean,
    pureBlack: Boolean,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
    specVersion: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2026,
    platform: DynamicScheme.Platform = DynamicScheme.Platform.PHONE
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
        PaletteStyle.TonalSpot -> SchemeTonalSpot(
            hct,
            isDark,
            contrastLevel,
            specVersion,
            platform
        )

        PaletteStyle.Neutral -> SchemeNeutral(hct, isDark, contrastLevel, specVersion, platform)
        PaletteStyle.Vibrant -> SchemeVibrant(hct, isDark, contrastLevel, specVersion, platform)
        PaletteStyle.Expressive -> SchemeExpressive(
            hct,
            isDark,
            contrastLevel,
            specVersion,
            platform
        )

        PaletteStyle.Rainbow -> SchemeRainbow(hct, isDark, contrastLevel, specVersion, platform)
        PaletteStyle.FruitSalad -> SchemeFruitSalad(
            hct,
            isDark,
            contrastLevel,
            specVersion,
            platform
        )

        PaletteStyle.Monochrome -> SchemeMonochrome(
            hct,
            isDark,
            contrastLevel,
            specVersion,
            platform
        )

        PaletteStyle.Fidelity -> SchemeFidelity(
            hct,
            isDark,
            contrastLevel,
            specVersion,
            platform
        )

        PaletteStyle.Content -> SchemeContent(hct, isDark, contrastLevel, specVersion, platform)
    }

    return ColorScheme(
        primary = scheme.primary
            .toColor()
            .applyPureBlack(0.3f),
        onPrimary = scheme.onPrimary
            .toColor()
            .applyPureBlack(0.3f),
        primaryContainer = scheme.primaryContainer
            .toColor()
            .applyPureBlack(0.3f),
        onPrimaryContainer = scheme.onPrimaryContainer
            .toColor()
            .applyPureBlack(0.3f),
        inversePrimary = scheme.inversePrimary
            .toColor()
            .applyPureBlack(0.1f),
        secondary = scheme.secondary
            .toColor()
            .applyPureBlack(0.3f),
        onSecondary = scheme.onSecondary
            .toColor()
            .applyPureBlack(0.3f),
        secondaryContainer = scheme.secondaryContainer
            .toColor()
            .applyPureBlack(0.3f),
        onSecondaryContainer = scheme.onSecondaryContainer
            .toColor()
            .applyPureBlack(0.3f),
        tertiary = scheme.tertiary
            .toColor()
            .applyPureBlack(0.3f),
        onTertiary = scheme.onTertiary
            .toColor()
            .applyPureBlack(0.3f),
        tertiaryContainer = scheme.tertiaryContainer
            .toColor()
            .applyPureBlack(0.3f),
        onTertiaryContainer = scheme.onTertiaryContainer
            .toColor()
            .applyPureBlack(0.2f),
        background = scheme.background
            .toColor()
            .replaceByPureBlack(Color.Black),
        onBackground = scheme.onBackground
            .toColor()
            .applyPureBlack(0.15f),
        surface = scheme.surface
            .toColor()
            .replaceByPureBlack(Color.Black),
        onSurface = scheme.onSurface
            .toColor()
            .applyPureBlack(0.15f),
        surfaceVariant = scheme.surfaceVariant
            .toColor(),
        onSurfaceVariant = scheme.onSurfaceVariant
            .toColor(),
        surfaceTint = scheme.surfaceTint
            .toColor(),
        inverseSurface = scheme.inverseSurface
            .toColor()
            .applyPureBlack(0.3f), // 这个和下面的修改为了让snackbar的对比度正常
        inverseOnSurface = scheme.inverseOnSurface
            .toColor()
            .applyPureBlack(0.3f),
        error = scheme.error
            .toColor()
            .applyPureBlack(0.3f),
        onError = scheme.onError
            .toColor()
            .applyPureBlack(0.3f),
        errorContainer = scheme.errorContainer
            .toColor()
            .applyPureBlack(0.3f),
        onErrorContainer = scheme.onErrorContainer
            .toColor()
            .applyPureBlack(0.3f),
        outline = scheme.outline
            .toColor()
            .applyPureBlack(0.2f),
        outlineVariant = scheme.outlineVariant
            .toColor()
            .applyPureBlack(0.2f),
        scrim = scheme.scrim
            .toColor(),
        surfaceBright = scheme.surfaceBright
            .toColor()
            .applyPureBlack(0.3f),
        surfaceDim = scheme.surfaceDim
            .toColor()
            .applyPureBlack(0.2f),
        surfaceContainer = scheme.surfaceContainer
            .toColor()
            .replaceByPureBlack(Color.Black),
        surfaceContainerHigh = scheme.surfaceContainerHigh
            .toColor()
            .applyPureBlack(0.2f),
        surfaceContainerHighest = scheme.surfaceContainerHighest
            .toColor()
            .applyPureBlack(0.2f),
        surfaceContainerLow = scheme.surfaceContainerLow
            .toColor()
            .applyPureBlack(0.2f),
        surfaceContainerLowest = scheme.surfaceContainerLowest
            .toColor()
            .applyPureBlack(0.2f),
        primaryFixed = scheme.primaryFixed.toColor(),
        primaryFixedDim = scheme.primaryFixedDim.toColor(),
        onPrimaryFixed = scheme.onPrimaryFixed.toColor(),
        onPrimaryFixedVariant = scheme.onPrimaryFixedVariant.toColor(),
        secondaryFixed = scheme.secondaryFixed.toColor(),
        secondaryFixedDim = scheme.secondaryFixedDim.toColor(),
        onSecondaryFixed = scheme.onSecondaryFixed.toColor(),
        onSecondaryFixedVariant = scheme.onSecondaryFixedVariant.toColor(),
        tertiaryFixed = scheme.tertiaryFixed.toColor(),
        tertiaryFixedDim = scheme.tertiaryFixedDim.toColor(),
        onTertiaryFixed = scheme.onTertiaryFixed.toColor(),
        onTertiaryFixedVariant = scheme.onTertiaryFixedVariant.toColor(),
    )
}

@Composable
fun rememberDynamicColorScheme(
    keyColor: Color,
    isDark: Boolean = false,
    pureBlack: Boolean = false,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
    specVersion: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2026,
    platform: DynamicScheme.Platform = DynamicScheme.Platform.PHONE
): ColorScheme = remember(
    keyColor,
    isDark,
    pureBlack,
    style,
    contrastLevel,
    specVersion,
    platform
) { dynamicColorScheme(keyColor, isDark, pureBlack, style, contrastLevel, specVersion, platform) }

// 参考：https://github.com/jordond/MaterialKolor/blob/2c3dcea7b9372fe9642e1fd57c6ce29feb62b9c7/material-kolor/src/commonMain/kotlin/com/materialkolor/ktx/ColorScheme.kt
@Composable
fun animateColorScheme(
    colorScheme: ColorScheme,
    animationSpec: @Composable Transition.Segment<ColorScheme>.() -> FiniteAnimationSpec<Color> = { spring() }
): ColorScheme {
    val transition = updateTransition(colorScheme)

    val primary by transition.animateColor(animationSpec) { it.primary }
    val onPrimary by transition.animateColor(animationSpec) { it.onPrimary }
    val primaryContainer by transition.animateColor(animationSpec) { it.primaryContainer }
    val onPrimaryContainer by transition.animateColor(animationSpec) { it.onPrimaryContainer }
    val inversePrimary by transition.animateColor(animationSpec) { it.inversePrimary }
    val secondary by transition.animateColor(animationSpec) { it.secondary }
    val onSecondary by transition.animateColor(animationSpec) { it.onSecondary }
    val secondaryContainer by transition.animateColor(animationSpec) { it.secondaryContainer }
    val onSecondaryContainer by transition.animateColor(animationSpec) { it.onSecondaryContainer }
    val tertiary by transition.animateColor(animationSpec) { it.tertiary }
    val onTertiary by transition.animateColor(animationSpec) { it.onTertiary }
    val tertiaryContainer by transition.animateColor(animationSpec) { it.tertiaryContainer }
    val onTertiaryContainer by transition.animateColor(animationSpec) { it.onTertiaryContainer }
    val background by transition.animateColor(animationSpec) { it.background }
    val onBackground by transition.animateColor(animationSpec) { it.onBackground }
    val surface by transition.animateColor(animationSpec) { it.surface }
    val onSurface by transition.animateColor(animationSpec) { it.onSurface }
    val surfaceVariant by transition.animateColor(animationSpec) { it.surfaceVariant }
    val onSurfaceVariant by transition.animateColor(animationSpec) { it.onSurfaceVariant }
    val surfaceTint by transition.animateColor(animationSpec) { it.surfaceTint }
    val inverseSurface by transition.animateColor(animationSpec) { it.inverseSurface }
    val inverseOnSurface by transition.animateColor(animationSpec) { it.inverseOnSurface }
    val error by transition.animateColor(animationSpec) { it.error }
    val onError by transition.animateColor(animationSpec) { it.onError }
    val errorContainer by transition.animateColor(animationSpec) { it.errorContainer }
    val onErrorContainer by transition.animateColor(animationSpec) { it.onErrorContainer }
    val outline by transition.animateColor(animationSpec) { it.outline }
    val outlineVariant by transition.animateColor(animationSpec) { it.outlineVariant }
    val scrim by transition.animateColor(animationSpec) { it.scrim }
    val surfaceBright by transition.animateColor(animationSpec) { it.surfaceBright }
    val surfaceDim by transition.animateColor(animationSpec) { it.surfaceDim }
    val surfaceContainer by transition.animateColor(animationSpec) { it.surfaceContainer }
    val surfaceContainerHigh by transition.animateColor(animationSpec) { it.surfaceContainerHigh }
    val surfaceContainerHighest by transition.animateColor(animationSpec) { it.surfaceContainerHighest }
    val surfaceContainerLow by transition.animateColor(animationSpec) { it.surfaceContainerLow }
    val surfaceContainerLowest by transition.animateColor(animationSpec) { it.surfaceContainerLowest }
    val primaryFixed by transition.animateColor(animationSpec) { it.primaryFixed }
    val primaryFixedDim by transition.animateColor(animationSpec) { it.primaryFixedDim }
    val onPrimaryFixed by transition.animateColor(animationSpec) { it.onPrimaryFixed }
    val onPrimaryFixedVariant by transition.animateColor(animationSpec) { it.onPrimaryFixedVariant }
    val secondaryFixed by transition.animateColor(animationSpec) { it.secondaryFixed }
    val secondaryFixedDim by transition.animateColor(animationSpec) { it.secondaryFixedDim }
    val onSecondaryFixed by transition.animateColor(animationSpec) { it.onSecondaryFixed }
    val onSecondaryFixedVariant by transition.animateColor(animationSpec) { it.onSecondaryFixedVariant }
    val tertiaryFixed by transition.animateColor(animationSpec) { it.tertiaryFixed }
    val tertiaryFixedDim by transition.animateColor(animationSpec) { it.tertiaryFixedDim }
    val onTertiaryFixed by transition.animateColor(animationSpec) { it.onTertiaryFixed }
    val onTertiaryFixedVariant by transition.animateColor(animationSpec) { it.onTertiaryFixedVariant }

    return colorScheme.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = surfaceTint,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer,
        outline = outline,
        outlineVariant = outlineVariant,
        scrim = scrim,
        surfaceBright = surfaceBright,
        surfaceDim = surfaceDim,
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainerLowest = surfaceContainerLowest,
        primaryFixed = primaryFixed,
        primaryFixedDim = primaryFixedDim,
        onPrimaryFixed = onPrimaryFixed,
        onPrimaryFixedVariant = onPrimaryFixedVariant,
        secondaryFixed = secondaryFixed,
        secondaryFixedDim = secondaryFixedDim,
        onSecondaryFixed = onSecondaryFixed,
        onSecondaryFixedVariant = onSecondaryFixedVariant,
        tertiaryFixed = tertiaryFixed,
        tertiaryFixedDim = tertiaryFixedDim,
        onTertiaryFixed = onTertiaryFixed,
        onTertiaryFixedVariant = onTertiaryFixedVariant
    )
}
