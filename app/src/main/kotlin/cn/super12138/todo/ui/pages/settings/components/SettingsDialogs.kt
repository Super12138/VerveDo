package cn.super12138.todo.ui.pages.settings.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.components.BasicDialog
import cn.super12138.todo.ui.theme.shapeByInteraction
import cn.super12138.todo.utils.VibrationUtils

@Composable
fun SettingsRadioDialog(
    visible: Boolean,
    title: String,
    currentOptions: SettingsRadioOptions,
    options: List<SettingsRadioOptions>,
    onSelect: (id: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsDialog(
        visible = visible,
        title = title,
        text = {
            // Modifier.selectableGroup() 用来确保无障碍功能运行正确
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .selectableGroup()
                    .verticalScroll(rememberScrollState())
            ) {
                options.forEach { option ->
                    RadioItem(
                        selected = option == currentOptions,
                        text = option.text,
                        onClick = {
                            onSelect(option.id)
                            onDismiss()
                        }
                    )
                }
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RadioItem(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(
                shapeByInteraction(
                    shapes = VerveDoDefaults.shapes(),
                    pressed = pressed,
                    animationSpec = VerveDoDefaults.shapesDefaultAnimationSpec
                )
            )
            // .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .selectable(
                interactionSource = interactionSource,
                selected = selected,
                onClick = {
                    VibrationUtils.performHapticFeedback(view)
                    onClick()
                },
                role = Role.RadioButton
            )
            .padding(horizontal = VerveDoDefaults.screenHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // 设置为 null 有利于屏幕阅读器
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    title: String,
    text: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit = {}
) {
    BasicDialog(
        visible = visible,
        title = { Text(title) },
        text = text,
        confirmButton = {},
        dismissButton = {},
        onDismissRequest = onDismissRequest,
        modifier = modifier
    )
}