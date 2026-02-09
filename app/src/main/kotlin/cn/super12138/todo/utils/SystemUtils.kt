package cn.super12138.todo.utils

import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SystemUtils {
    /**
     * 获取应用版本号
     * @return 版本名称（版本代码）
     */
    fun getAppVersion(context: Context): String {
        val pkgInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val verName = pkgInfo.versionName
        val verCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pkgInfo.longVersionCode.toInt()
        } else {
            pkgInfo.versionCode
        }
        return "$verName ($verCode)"
    }

    /**
     * 获取格式化后的当前时间
     * 参考 https://github.com/rafi0101/Android-Room-Database-Backup/blob/master/core/src/main/java/de/raphaelebner/roomdatabasebackup/core/RoomBackup.kt#L770
     * @return 当前时间
     */
    fun getTime(): String {
        val currentTime = Calendar.getInstance().time

        val sdf =
            if (Build.VERSION.SDK_INT <= 28) {
                SimpleDateFormat("yyyy-MM-dd-HH_mm_ss", Locale.getDefault())
            } else {
                SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault())
            }

        return sdf.format(currentTime)
    }

    /**
     * 获取当天的时间戳
     */
    fun getTodayEightAM(): Long = Calendar.getInstance().apply {
        // 将时间设置为当天的开始（00:00:00.000）
        // 兼容API24
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun ComponentActivity.configureEdgeToEdge() {
    enableEdgeToEdge()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Force the 3-button navigation bar to be transparent
        // See: https://developer.android.com/develop/ui/views/layout/edge-to-edge#create-transparent
        window.isNavigationBarContrastEnforced = false
    }
}