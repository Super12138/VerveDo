package cn.super12138.todo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.utils.VibrationUtils

@Composable
fun FilterChipGroup(
    selectedId: Int,
    items: List<ChipItem>,
    modifier: Modifier = Modifier,
    onSelectedChanged: (ChipItem) -> Unit = {}
) {
    val view = LocalView.current

    // 由于FlexBox在Chip尺寸变化时出现错误，故暂时保留FlowRow实现（2026.11.27）
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding)
    ) {
        items.forEach {
            with(it) {
                val selected = selectedId == id
                FilterChip(
                    selected = selected,
                    leadingIcon = if (selected) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = stringResource(R.string.tip_selected),
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    label = { Text(text = label, maxLines = 1) },
                    onClick = {
                        onSelectedChanged(this)
                        VibrationUtils.performHapticFeedback(view)
                    },
                    shapes = FilterChipDefaults.shapes()
                )
            }
        }
    }
}

/*@Composable
fun FilterChipGroup(
    selectedId: Int,
    items: List<ChipItem>,
    modifier: Modifier = Modifier,
    onSelectedChanged: (ChipItem) -> Unit = {}
) {
    val view = LocalView.current
    FlexBox(
        modifier = modifier,
        config = {
            direction(FlexDirection.Row)
            justifyContent(FlexJustifyContent.Start)
            alignContent(FlexAlignContent.Start)
            wrap(FlexWrap.Wrap)
            gap(VerveDoDefaults.contentPadding)
        }
    ) {
        items.forEach {
            with(it) {
                val selected = selectedId == id
                FilterChip(
                    selected = selected,
                    leadingIcon = if (selected) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = stringResource(R.string.tip_selected),
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    label = { Text(text = label, maxLines = 1) },
                    onClick = {
                        onSelectedChanged(this)
                        VibrationUtils.performHapticFeedback(view)
                    },
                    shapes = FilterChipDefaults.shapes()
                )
            }
        }
    }
}*/

@Immutable
data class ChipItem(
    val id: Int,
    val label: String
)