package cn.super12138.todo.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import cn.super12138.todo.utils.VibrationUtils

/**
 * 封装的内置组件 FAB，在点击时支持添加震动反馈
 * * （现在内置组件的动画好像好一点了）
 *
 * @param iconRes FAB 的前置图标
 * @param text FAB 的文本
 * @param expanded 是否为展开状态
 * @param containerColor FAB 容器的颜色
 * @param contentColor FAB 文本和图标颜色，通常不需要自己指定，会自动依据容器颜色设置
 * @param elevation FAB 的高度（阴影大小）
 * @param onClick 点击 FAB 后的回调
 * @param modifier `Modifier` 修改器
 */
@Composable
fun TodoFloatingActionButton(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    shape: Shape = FloatingActionButtonDefaults.smallExtendedFabShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
) {
    val view = LocalView.current
    SmallExtendedFloatingActionButton(
        text = {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        },
        icon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null
            )
        },
        onClick = {
            VibrationUtils.performHapticFeedback(view)
            onClick()
        },
        elevation = elevation,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = shape,
        interactionSource = interactionSource,
        expanded = expanded,
        modifier = modifier
    )
}