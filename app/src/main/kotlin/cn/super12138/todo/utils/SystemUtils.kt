package cn.super12138.todo.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.pm.PackageInfoCompat
import cn.super12138.todo.ui.activities.MainActivity
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.system.exitProcess
import kotlin.time.Clock
import kotlin.time.Instant

object SystemUtils {
    /**
     * 获取格式化后的当前时间（yyyy-MM-dd-HH:mm:ss）
     */
    fun getFormattedCurrentTime(): String =
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .format(
                LocalDateTime.Format {
                    year()
                    char('-')
                    monthNumber()
                    char('-')
                    day()
                    char('-')
                    hour()
                    char('-')
                    minute()
                    char('-')
                    second()
                }
            )

    /**
     * 获取用户当天开始的UTC时间，返回Kotlin Instant
     */
    fun startOfUTCToday(): Instant = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date.atStartOfDayIn(TimeZone.UTC)
}

fun ComponentActivity.configureEdgeToEdge() {
    enableEdgeToEdge()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Force the 3-button navigation bar to be transparent
        // See: https://developer.android.com/develop/ui/views/layout/edge-to-edge#create-transparent
        window.isNavigationBarContrastEnforced = false
    }
}

fun Context.restartApp() {
    val intent = Intent(
        this,
        MainActivity::class.java
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    this.startActivity(intent)
    exitProcess(0)
}

/**
 * 获取应用版本号
 * @return 版本名称（版本代码）
 */
fun Context.appVersion(): String {
    val pkgInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    val verName = pkgInfo.versionName
    val verCode = PackageInfoCompat.getLongVersionCode(pkgInfo).toInt()
    return "$verName ($verCode)"
}
