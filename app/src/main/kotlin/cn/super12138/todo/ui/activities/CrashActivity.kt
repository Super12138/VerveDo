package cn.super12138.todo.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.ui.pages.crash.CrashPage
import cn.super12138.todo.ui.theme.VerveDoTheme
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.configureEdgeToEdge
import cn.super12138.todo.utils.isDark
import org.koin.compose.viewmodel.koinViewModel

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
            val mainViewModel: MainViewModel = koinViewModel()

            val appearanceUiState by mainViewModel.appearanceUiState.collectAsStateWithLifecycle()
            val hapticFeedback by mainViewModel.hapticFeedbackFlow.collectAsStateWithLifecycle(
                Constants.PREF_HAPTIC_FEEDBACK_DEFAULT
            )

            val isDark = appearanceUiState.darkMode.isDark()
            // 配置状态栏和底部导航栏的颜色（在用户切换深色模式时）
            // https://github.com/dn0ne/lotus/blob/master/app/src/main/java/com/dn0ne/player/MainActivity.kt#L266
            LaunchedEffect(appearanceUiState.darkMode) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }

            LaunchedEffect(hapticFeedback) { VibrationUtils.setEnabled(hapticFeedback) }

            VerveDoTheme(
                darkTheme = isDark,
                pureBlackMode = appearanceUiState.pureBlackMode,
                style = appearanceUiState.paletteStyle,
                contrastLevel = appearanceUiState.contrastLevel,
                dynamicColor = appearanceUiState.dynamicColor
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