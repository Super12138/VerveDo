package cn.super12138.todo.ui.pages.overview.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cn.super12138.todo.R
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.components.EmptyTip
import cn.super12138.todo.ui.components.EmptyTipType
import cn.super12138.todo.ui.pages.tasks.components.CategoryBadge
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.utils.containerColor
import cn.super12138.todo.utils.toRelativeTimeString

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListCard(
    title: String,
    list: List<TaskEntity>,
    modifier: Modifier = Modifier,
    containerColor: Color = VerveDoDefaults.Colors.Container,
    shape: CornerBasedShape = VerveDoDefaults.defaultShape,
    colors: CardColors = CardDefaults.cardColors(containerColor = containerColor),
    emptyTipContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    val transitionSpec = fadeScale()

    Card(
        modifier = modifier.height(VerveDoDefaults.Sizes.overviewCardHeight * 2),
        colors = colors,
        shape = shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VerveDoDefaults.screenHorizontalPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            AnimatedContent(
                targetState = list.isEmpty(),
                transitionSpec = { transitionSpec }
            ) {
                if (it) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f) // 占满剩余空间
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyTip(
                            type = EmptyTipType.List,
                            containerColor = emptyTipContainerColor,
                            shape = MaterialShapes.Cookie7Sided.toShape()
                        )

                        Text(
                            text = stringResource(R.string.tip_no_task_brief),
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding)) {
                        items(
                            items = list,
                            key = { task -> task.id }
                        ) { task ->
                            UpcomingTaskItem(
                                content = task.content,
                                category = task.category,
                                priority = Priority.fromFloat(task.priority),
                                dueDateMillis = task.dueDateMillis
                            )
                        }
                    }
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
    dueDateMillis: Long?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = VerveDoDefaults.settingsItemVerticalPadding / 4),
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
                modifier = Modifier.weight(weight = 1f, fill = false)
            )

            Text(
                text = dueDateMillis?.toRelativeTimeString(context) ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.padding(start = VerveDoDefaults.screenVerticalPadding)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            CategoryBadge(
                category = category,
                modifier = Modifier.weight(weight = 1f, fill = false)
            )

            Text(
                text = stringResource(priority.nameRes),
                style = MaterialTheme.typography.labelMedium.copy(priority.containerColor()),
                modifier = Modifier.padding(start = VerveDoDefaults.screenVerticalPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UpcomingLongTaskItemPreview() {
    UpcomingTaskItem(
        content = "这里有一条很长长长长长长长长长长长长长长长长长长长长长长的任务",
        category = "这里有一条很长长长长长长长长长长长长长长长长长长长长长长的分类",
        dueDateMillis = 1787616000000,
        priority = Priority.NotImportant,
    )
}

@Preview(showBackground = true)
@Composable
private fun UpcomingTaskItemPreview() {
    UpcomingTaskItem(
        content = "这是一个任务",
        category = "它的分类",
        dueDateMillis = 1787616000000,
        priority = Priority.NotImportant,
    )
}
