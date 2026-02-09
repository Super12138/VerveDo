package cn.super12138.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.ui.TodoDefaults

enum class EmptyTipType {
    Search,
    List,
    TaskCompleted
}

@Composable
fun EmptyTip(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    type: EmptyTipType
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(TodoDefaults.screenHorizontalPadding)
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Icon(
            painter = painterResource(
                id = when (type) {
                    EmptyTipType.List -> R.drawable.ic_list_no_item
                    EmptyTipType.Search -> R.drawable.ic_search_not_found
                    EmptyTipType.TaskCompleted -> R.drawable.ic_thumb_up
                }
            ),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(size / 2)
        )
    }
}