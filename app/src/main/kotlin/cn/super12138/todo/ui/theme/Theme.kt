package cn.super12138.todo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import cn.super12138.todo.logic.model.DynamicSchemePlatform
import cn.super12138.todo.logic.model.ColorSpecVersion
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.logic.model.toPlatform
import cn.super12138.todo.logic.model.toSpecVersion
import com.kyant.m3color.dynamiccolor.DynamicScheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VerveDoTheme(
    color: Color? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlackMode: Boolean = false,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: ContrastLevel = ContrastLevel.Default,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    specVersion: ColorSpecVersion = ColorSpecVersion.Spec2025,
    platform: DynamicSchemePlatform = DynamicSchemePlatform.Phone,
    content: @Composable () -> Unit
) {
    val baseColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicColor) {
        colorResource(id = android.R.color.system_accent1_500)
    } else {
        Color(0xFF0061A4)
    }

    // 关键色，如果指定就使用
    val keyColor = color ?: baseColor

    val colorScheme = dynamicColorScheme(
        keyColor = keyColor,
        isDark = darkTheme,
        pureBlack = pureBlackMode,
        style = style,
        contrastLevel = contrastLevel.value.toDouble(),
        specVersion = specVersion.toSpecVersion(),
        platform = platform.toPlatform()
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}