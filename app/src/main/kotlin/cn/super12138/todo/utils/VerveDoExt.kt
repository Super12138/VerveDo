package cn.super12138.todo.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.FloatRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.ColorUtils
import cn.super12138.todo.R
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.logic.model.SortingMethod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

fun Int.blend(
    color: Int,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.5f,
): Int = ColorUtils.blendARGB(this, color, fraction)

@Stable
@Composable
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
fun Long.toFormattedDate(): String {
    val date = this.toInstant().toLocalDateTime(TimeZone.UTC)
    return date.format(
        LocalDateTime.Format {
            year()
            char('-')
            monthNumber()
            char('-')
            day()
        }
    )
}

/**
 * 将时间戳转换为相对时间字符串
 *
 * @receiver Long? 时间戳（单位为毫秒）或 null
 * @param context 上下文，用于获取字符串资源
 * @return String 格式化后的相对时间字符串。如果为传入参数为null则返回空字符串，反之根据时间差返回相应的字符串，如“今天”、“明天”、“3天后”、“2周后”、“1个月后”、“1年后”等
 */
fun Long.toRelativeTimeString(context: Context): String {
    val date = this.toInstant().toLocalDateTime(TimeZone.UTC).date.atStartOfDayIn(TimeZone.UTC)
    val today = SystemUtils.startOfUTCToday()

    return with(context) {
        when (val duration = date - today) {
            in 0.days..0.days -> getString(R.string.time_today)

            // 将来的时间
            in 1.days..1.days -> getString(R.string.time_tomorrow)
            in 2.days..6.days -> getString(
                R.string.time_in_days,
                (duration.inWholeDays).toInt()
            )

            in 7.days..29.days -> getString(
                R.string.time_in_weeks,
                (duration.inWholeDays / 7).toInt()
            )

            in 30.days..364.days -> getString(
                R.string.time_in_months,
                (duration.inWholeDays / 30).toInt()
            )

            in 365.days..Duration.INFINITE -> getString(
                R.string.time_in_years,
                (duration.inWholeDays / 365).toInt()
            )

            // 过去的时间
            in (-1).days..(-1).days -> getString(R.string.time_yesterday)
            in (-6).days..(-2).days -> getString(
                R.string.time_days_ago,
                (-duration.inWholeDays).toInt()
            )

            in (-29).days..(-7).days -> getString(
                R.string.time_weeks_ago,
                (-duration.inWholeDays / 7).toInt()
            )

            in (-364).days..(-30).days -> getString(
                R.string.time_months_ago,
                (-duration.inWholeDays / 30).toInt()
            )

            in -Duration.INFINITE..(-365).days -> getString(
                R.string.time_years_ago,
                (-duration.inWholeDays / 365).toInt()
            )

            else -> getString(R.string.time_today)
        }
    }
}

@Composable
fun disabledContentColor(alpha: Float = 0.38f): Color =
    MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)

@Composable
fun disabledContainerColor(alpha: Float = 0.12f): Color =
    MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)

fun List<TaskEntity>.sort(sortingMethod: SortingMethod): List<TaskEntity> = when (sortingMethod) {
    SortingMethod.Sequential -> this.sortedWith(
        comparator = compareBy<TaskEntity> { it.isCompleted } // 必须先要按照是否完成排序
            .thenBy { it.id }
    )

    SortingMethod.Category -> this.sortedWith(
        comparator = compareBy<TaskEntity> { it.isCompleted }
            .thenBy { it.category }
    )

    SortingMethod.Priority -> this.sortedWith(
        comparator = compareBy<TaskEntity> { it.isCompleted }
            .thenByDescending { it.priority }
            .thenBy(nullsLast()) { it.dueDateMillis }
    ) // 优先级高的在前

    SortingMethod.Completion -> this.sortedWith(
        comparator = compareBy<TaskEntity> { it.isCompleted }
            .thenBy { it.category }
            .thenByDescending { it.priority }
    ) // 未完成的在前
    SortingMethod.AlphabeticalAscending -> this.sortedWith(
        comparator = compareBy<TaskEntity> { it.isCompleted }
            .thenBy { it.content }
            .thenByDescending { it.priority }
    )

    SortingMethod.AlphabeticalDescending -> this.sortedWith(
        comparator = compareBy<TaskEntity> { it.isCompleted }
            .thenByDescending { it.content }
            .thenByDescending { it.priority }
    )

    SortingMethod.DueDate -> this.sortedWith(
        comparator = compareBy<TaskEntity> { it.isCompleted }
            // 确保未设置截止日期的任务在最下头
            .thenBy(nullsLast()) { it.dueDateMillis }
    )
}

@Composable
fun Boolean.keyColorBasedOnDynamicColor() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && this) {
        colorResource(id = android.R.color.system_accent1_500)
    } else {
        Color(0xFF0061A4)
    }

@Composable
infix fun Int.toggleButtonShapesIn(list: List<Any>) = when (this) {
    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
    list.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
}

@Composable
fun DarkMode.isDark() = when (this) {
    DarkMode.FollowSystem -> isSystemInDarkTheme()
    DarkMode.Light -> false
    DarkMode.Dark -> true
}

@Suppress("NOTHING_TO_INLINE")
inline fun Int.toColor(): Color = Color(this)

// https://github.com/jordond/MaterialKolor/blob/main/material-kolor/src/commonMain/kotlin/com/materialkolor/DynamicMaterialTheme.kt
@Composable
fun Color.animate(animationSpec: AnimationSpec<Color> = spring()): Color =
    animateColorAsState(this, animationSpec).value

// https://github.com/hushenghao/AndroidEasterEggs/blob/main/core/theme/src/main/java/com/dede/android_eggs/views/theme/Theme.kt#L21
/**
 * 让颜色变暗
 */
fun Color.darken(fraction: Float = 0.5f): Color =
    Color(this.toArgb().blend(Color.Black.toArgb(), fraction))

fun Color.replace(color: Color): Color = color
fun Color.replace(color: Int): Color = color.toColor()

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()

fun Long.toInstant() = Instant.fromEpochMilliseconds(this)
