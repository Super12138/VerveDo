package cn.super12138.todo.ui.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.ui.TodoDefaults
import cn.super12138.todo.ui.components.Konfetti
import cn.super12138.todo.ui.navigation.TodoDestinations
import cn.super12138.todo.ui.navigation.TopNavigation
import cn.super12138.todo.ui.theme.ToDoTheme
import cn.super12138.todo.ui.viewmodels.MainViewModel
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.configureEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        configureEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = viewModel()

            val showConfetti = mainViewModel.showConfetti

            val mainBackStack = mainViewModel.mainBackStack
            val navigationScaffoldState = rememberNavigationSuiteScaffoldState()

            // 主题
            val dynamicColor by DataStoreManager.dynamicColorFlow.collectAsState(initial = Constants.PREF_DYNAMIC_COLOR_DEFAULT)
            val paletteStyle by DataStoreManager.paletteStyleFlow.collectAsState(initial = Constants.PREF_PALETTE_STYLE_DEFAULT)
            val contrastLevel by DataStoreManager.contrastLevelFlow.collectAsState(initial = Constants.PREF_CONTRAST_LEVEL_DEFAULT)
            val darkMode by DataStoreManager.darkModeFlow.collectAsState(initial = Constants.PREF_DARK_MODE_DEFAULT)
            val secureMode by DataStoreManager.secureModeFlow.collectAsState(initial = Constants.PREF_SECURE_MODE_DEFAULT)
            val hapticFeedback by DataStoreManager.hapticFeedbackFlow.collectAsState(initial = Constants.PREF_HAPTIC_FEEDBACK_DEFAULT)

            // 深色模式
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

            // 阻止截屏相关配置
            LaunchedEffect(secureMode) {
                if (secureMode) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            LaunchedEffect(hapticFeedback) {
                VibrationUtils.setEnabled(hapticFeedback)
            }

            // 当BackStack出现非顶层路由时，隐藏底部导航栏
            LaunchedEffect(mainBackStack.backStack.lastOrNull()) {
                val isTopLevel =
                    mainBackStack.backStack.lastOrNull() in TodoDestinations.entries.map { it.route }
                if (isTopLevel) {
                    if (navigationScaffoldState.currentValue != NavigationSuiteScaffoldValue.Visible) navigationScaffoldState.show()
                } else {
                    if (navigationScaffoldState.currentValue != NavigationSuiteScaffoldValue.Hidden) navigationScaffoldState.hide()
                }
            }

            ToDoTheme(
                darkTheme = darkTheme,
                style = PaletteStyle.fromId(paletteStyle),
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = dynamicColor
            ) {
                Surface(
                    color = TodoDefaults.Colors.Background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val view = LocalView.current
                    NavigationSuiteScaffold(
                        state = navigationScaffoldState,
                        navigationSuiteItems = {
                            TodoDestinations.entries.forEach { destination ->
                                val selected = destination.route == mainBackStack.topLevelKey
                                item(
                                    icon = {
                                        Crossfade(selected) {
                                            if (it) {
                                                Icon(
                                                    painter = painterResource(destination.selectedIcon),
                                                    contentDescription = null
                                                )
                                            } else {
                                                Icon(
                                                    painter = painterResource(destination.icon),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    },
                                    label = { Text(stringResource(destination.label)) },
                                    selected = selected,
                                    onClick = {
                                        VibrationUtils.performHapticFeedback(view)
                                        mainBackStack.addTopLevel(destination.route)
                                    }
                                )
                            }
                        },
                        containerColor = TodoDefaults.Colors.Background,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TopNavigation(
                            backStack = mainBackStack,
                            viewModel = mainViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Konfetti(
                        state = showConfetti,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}