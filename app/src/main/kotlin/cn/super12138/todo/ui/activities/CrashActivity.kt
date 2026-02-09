package cn.super12138.todo.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.ui.pages.crash.CrashPage
import cn.super12138.todo.ui.theme.VerveDoTheme
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.configureEdgeToEdge

class CrashActivity : ComponentActivity() {
    companion object {
        const val BRAND_PREFIX = "Brand:      "
        const val MODEL_PREFIX = "Model:      "
        const val DEVICE_SDK_PREFIX = "Device SDK: "
        const val CRASH_TIME_PREFIX = "Crash time: "
        const val BEGINNING_CRASH = "======beginning of crash======"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        configureEdgeToEdge()
        super.onCreate(savedInstanceState)

        val crashLogs = intent.getStringExtra("crash_logs")

        setContent {
            val dynamicColor by DataStoreManager.dynamicColorFlow.collectAsState(initial = Constants.PREF_DYNAMIC_COLOR_DEFAULT)
            val paletteStyle by DataStoreManager.paletteStyleFlow.collectAsState(initial = Constants.PREF_PALETTE_STYLE_DEFAULT)
            val contrastLevel by DataStoreManager.contrastLevelFlow.collectAsState(initial = Constants.PREF_CONTRAST_LEVEL_DEFAULT)
            val darkMode by DataStoreManager.darkModeFlow.collectAsState(initial = Constants.PREF_DARK_MODE_DEFAULT)
            val hapticFeedback by DataStoreManager.hapticFeedbackFlow.collectAsState(initial = Constants.PREF_HAPTIC_FEEDBACK_DEFAULT)

            val darkTheme = when (DarkMode.fromId(darkMode)) {
                DarkMode.FollowSystem -> isSystemInDarkTheme()
                DarkMode.Light -> false
                DarkMode.Dark -> true
            }
            // 配置状态栏和底部导航栏的颜色（在用户切换深色模式时）
            // https://github.com/dn0ne/lotus/blob/master/app/src/main/java/com/dn0ne/player/MainActivity.kt#L266
            LaunchedEffect(darkMode) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            LaunchedEffect(hapticFeedback) {
                VibrationUtils.setEnabled(hapticFeedback)
            }

            VerveDoTheme(
                darkTheme = darkTheme,
                style = PaletteStyle.fromId(paletteStyle),
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = dynamicColor
            ) {
                CrashPage(
                    crashLog = crashLogs ?: stringResource(R.string.tip_no_crash_logs),
                    exitApp = ::finishAffinity,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}