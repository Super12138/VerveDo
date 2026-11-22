package cn.super12138.todo.ui.components

import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.isDark

@Composable
fun ActivityConfigure(
    window: Window,
    darkMode: DarkMode,
    hapticFeedback: Boolean,
    secureMode: Boolean = false
) {
    val isDark = darkMode.isDark()

    // 配置状态栏和底部导航栏的颜色（在用户切换深色模式时）
    // https://github.com/dn0ne/lotus/blob/master/app/src/main/java/com/dn0ne/player/MainActivity.kt#L266
    SideEffect(darkMode) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
    }

    SideEffect(hapticFeedback) { VibrationUtils.setEnabled(hapticFeedback) }

    // 安全模式相关配置
    SideEffect(secureMode) {
        if (secureMode) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}