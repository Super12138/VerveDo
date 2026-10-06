package cn.super12138.todo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.utils.keyColorBasedOnDynamicColor
import com.kyant.m3color.dynamiccolor.ColorSpec
import com.kyant.m3color.dynamiccolor.DynamicScheme

@Composable
fun VerveDoTheme(
    color: Color? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlackMode: Boolean = false,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: ContrastLevel = ContrastLevel.Default,
    dynamicColor: Boolean = true, // Dynamic color is available on Android 12+
    specVersion: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2025,
    platform: DynamicScheme.Platform = DynamicScheme.Platform.PHONE,
    animate: Boolean = true,
    content: @Composable () -> Unit
) {
    // 关键色，如果指定就使用
    val keyColor = color ?: dynamicColor.keyColorBasedOnDynamicColor()

    val colorScheme = rememberDynamicColorScheme(
        keyColor = keyColor,
        isDark = darkTheme,
        pureBlack = pureBlackMode,
        style = style,
        contrastLevel = contrastLevel.value.toDouble(),
        specVersion = specVersion,
        platform = platform
    )

    MaterialExpressiveTheme(
        colorScheme = if (animate) animateColorScheme(colorScheme) else colorScheme,
        typography = Typography,
        content = content
    )
}