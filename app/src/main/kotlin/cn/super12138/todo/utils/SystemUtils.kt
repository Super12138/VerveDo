package cn.super12138.todo.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import cn.super12138.todo.ui.activities.MainActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import kotlin.system.exitProcess

object SystemUtils {
    /**
     * 获取格式化后的当前时间
     * 参考 https://github.com/rafi0101/Android-Room-Database-Backup/blob/master/core/src/main/java/de/raphaelebner/roomdatabasebackup/core/RoomBackup.kt#L770
     * @return 当前时间
     */
    fun getTime(): String {
        val currentTime = Calendar.getInstance().time

        val sdf = if (Build.VERSION.SDK_INT <= 28) {
            SimpleDateFormat("yyyy-MM-dd-HH_mm_ss", Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault())
        }

        return sdf.format(currentTime)
    }

    /**
     * 获取当天的时间戳
     */
    fun getTodayEightAM(): Long {
        val today = LocalDate.now()
        val eightAM = today.atTime(8, 0)
        val zoneId = ZoneId.systemDefault()
        return eightAM.atZone(zoneId).toInstant().toEpochMilli()
    }
}

fun ComponentActivity.configureEdgeToEdge() {
    enableEdgeToEdge()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Force the 3-button navigation bar to be transparent
        // See: https://developer.android.com/develop/ui/views/layout/edge-to-edge#create-transparent
        window.isNavigationBarContrastEnforced = false
    }
}

/**
 * 重启应用
 * @param context 上下文
 */
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
    val verCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pkgInfo.longVersionCode.toInt()
    } else {
        pkgInfo.versionCode
    }
    return "$verName ($verCode)"
}
