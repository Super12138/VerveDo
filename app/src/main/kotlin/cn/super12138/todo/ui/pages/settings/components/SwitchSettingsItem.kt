package cn.super12138.todo.ui.pages.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.utils.VibrationUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwitchSettingsItem(
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int,
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingsItem(
        leadingIcon = painterResource(leadingIconRes),
        title = title,
        description = description,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null,
                modifier = Modifier.padding(start = VerveDoDefaults.settingsItemHorizontalPadding / 2)
            )
        },
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwitchSettingsItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    leadingIcon: ImageVector? = null,
    title: String,
    description: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    val view = LocalView.current
    SettingsItem(
        leadingIcon = leadingIcon,
        title = title,
        description = description,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = {
                    VibrationUtils.performHapticFeedback(view)
                    onCheckedChange(it)
                },
                modifier = Modifier.padding(start = VerveDoDefaults.settingsItemHorizontalPadding / 2)
            )
        },
        onClick = { onCheckedChange(!checked) },
        modifier = modifier
    )
}