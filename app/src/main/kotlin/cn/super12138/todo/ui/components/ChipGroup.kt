package cn.super12138.todo.ui.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.utils.VibrationUtils

/**
 * 部分参考：https://github.com/Rhythamtech/FilterChipGroup-Compose-Android/blob/main/FilterChipGroup.kt
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipGroup(
    modifier: Modifier = Modifier,
    items: List<ChipItem>,
    defaultSelectedItemIndex: Int = 0,
    onSelectedChanged: (Int) -> Unit = {}
) {
    val view = LocalView.current
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(defaultSelectedItemIndex) }

    LaunchedEffect(defaultSelectedItemIndex) {
        selectedItemIndex = defaultSelectedItemIndex
    }

    FlowRow(modifier = modifier) {
        items.forEach { item ->
            val selected = selectedItemIndex == item.id
            FilterChip(
                selected = selected,
                onClick = {
                    selectedItemIndex = item.id
                    VibrationUtils.performHapticFeedback(view)
                    onSelectedChanged(item.id)
                },
                label = {
                    Text(
                        text = item.name,
                        maxLines = 1
                    )
                },
                leadingIcon =
                    if (selected) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = stringResource(R.string.tip_selected),
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }
}

data class ChipItem(
    val id: Int,
    val name: String
)