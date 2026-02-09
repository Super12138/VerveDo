package cn.super12138.todo.ui.pages.overview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.logic.database.TodoEntity
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.TodoDefaults
import cn.super12138.todo.utils.containerColor
import cn.super12138.todo.utils.toRelativeTimeString

@Composable
fun UpcomingTaskCard(
    modifier: Modifier = Modifier,
    nextWeekTodo: List<TodoEntity>,
    containerColor: Color = TodoDefaults.Colors.Container
) {
    Card(
        modifier = modifier.height(TodoDefaults.overviewCardHeight * 2),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = TodoDefaults.defaultShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TodoDefaults.screenHorizontalPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.title_upcoming_task),
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = nextWeekTodo,
                    key = { it.id }
                ) {
                    UpcomingTaskItem(
                        content = it.content,
                        category = it.category,
                        priority = Priority.fromFloat(it.priority),
                        dueDate = it.dueDate
                    )
                }
            }
        }
    }
}

@Composable
fun UpcomingTaskItem(
    content: String,
    category: String,
    priority: Priority,
    dueDate: Long?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = TodoDefaults.settingsItemVerticalPadding / 4),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = dueDate.toRelativeTimeString(context),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = TodoDefaults.screenVerticalPadding)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                Text(
                    text = category.ifEmpty { stringResource(R.string.tip_default_category) },
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(priority.nameRes),
                style = MaterialTheme.typography.labelMedium.copy(priority.containerColor()),
                modifier = Modifier.padding(start = TodoDefaults.screenVerticalPadding)
            )
        }
    }
}