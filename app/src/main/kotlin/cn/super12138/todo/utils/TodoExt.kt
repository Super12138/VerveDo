package cn.super12138.todo.utils

import android.content.Context
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.Dp
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@Composable
@Stable
fun Priority.containerColor(): Color =
    when (this) {
        Priority.NotUrgent -> MaterialTheme.colorScheme.onSurfaceVariant
        Priority.NotImportant -> MaterialTheme.colorScheme.onSurfaceVariant
        Priority.Default -> MaterialTheme.colorScheme.secondary
        Priority.Important -> MaterialTheme.colorScheme.tertiary
        Priority.Urgent -> MaterialTheme.colorScheme.error
    }

/**
 * 获取部分圆角的形状
 *
 * @param topRounded 顶部是否圆角
 * @param bottomRounded 底部是否圆角
 * @param roundedShape 所需圆角形状
 */
@Composable
fun CornerBasedShape.getPartialRoundedShape(
    topRounded: Boolean,
    bottomRounded: Boolean,
    roundedShape: CornerBasedShape
): CornerBasedShape =
    this.copy(
        topStart = if (topRounded) roundedShape.topStart else this.topStart,
        topEnd = if (topRounded) roundedShape.topEnd else this.topEnd,
        bottomEnd = if (bottomRounded) roundedShape.bottomEnd else this.bottomEnd,
        bottomStart = if (bottomRounded) roundedShape.bottomStart else this.bottomStart,
    )

/**
 * 绘制渐变边缘遮罩
 *
 * @param edgeWidth 渐变边缘宽度
 * @param maskColor 遮罩颜色
 * @param leftEdge 是否在左侧边缘添加遮罩（否即在右侧边缘添加）
 */
fun ContentDrawScope.drawFadedEdge(
    edgeWidth: Dp,
    maskColor: Color,
    leftEdge: Boolean
) {
    val edgeWidthPx = edgeWidth.toPx()
    drawRect(
        topLeft = Offset(if (leftEdge) 0f else size.width - edgeWidthPx, 0f),
        size = Size(edgeWidthPx, size.height),
        brush =
            Brush.horizontalGradient(
                colors = listOf(Color.Transparent, maskColor),
                startX = if (leftEdge) 0f else size.width,
                endX = if (leftEdge) edgeWidthPx else size.width - edgeWidthPx
            ),
        blendMode = BlendMode.DstIn
    )
}

/**
 * 将时间戳转换为本地日期字符串
 *
 * @receiver Long? 时间戳（单位为毫秒）或 null
 * @return String 格式化后的日期字符串。如果为传入参数为null则返回空字符串，反之格式为 “yyyy-MM-dd”
 */
fun Long?.toLocalDateString(): String {
    if (this == null) return ""
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(date)
}

/**
 * 将时间戳转换为相对时间字符串
 *
 * @receiver Long? 时间戳（单位为毫秒）或 null
 * @param context 上下文，用于获取字符串资源
 * @return String 格式化后的相对时间字符串。如果为传入参数为null则返回空字符串，反之根据时间差返回相应的字符串，如“今天”、“明天”、“3天后”、“2周后”、“1个月后”、“1年后”等
 */
fun Long?.toRelativeTimeString(context: Context): String {
    if (this == null) return ""
    val today = SystemUtils.getTodayEightAM()
    val diff = (this - today).milliseconds

    return diff.toComponents { days, _, _, _, _ ->
        when {
            days.days == 1.days -> context.getString(R.string.time_tomorrow)
            days.days > 1.days && days.days < 7.days -> context.getString(
                R.string.time_in_days,
                days.toInt()
            )

            days.days in 7.days..<30.days -> context.getString(
                R.string.time_in_weeks,
                (days / 7).toInt()
            )

            days.days in 30.days..<365.days -> context.getString(
                R.string.time_in_months,
                (days / 30).toInt()
            )

            days.days >= 365.days -> context.getString(R.string.time_in_years, (days / 365).toInt())
            days.days == (-1).days -> context.getString(R.string.time_yesterday)
            days.days < (-1).days && days.days > (-7).days -> context.getString(
                R.string.time_days_ago,
                -days.toInt()
            )

            days.days in (-7).days..<(-30).days -> context.getString(
                R.string.time_weeks_ago,
                -(days / 7).toInt()
            )

            days.days in (-30).days..<(-365).days -> context.getString(
                R.string.time_months_ago,
                -(days / 30).toInt()
            )

            days.days <= (-365).days -> context.getString(
                R.string.time_years_ago,
                -(days / 365).toInt()
            )

            else -> context.getString(R.string.time_today)
        }
    }
}

@Composable
fun disabledContentColor(alpha: Float = 0.38f): Color =
    MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)

@Composable
fun disabledContainerColor(alpha: Float = 0.12f): Color =
    MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)