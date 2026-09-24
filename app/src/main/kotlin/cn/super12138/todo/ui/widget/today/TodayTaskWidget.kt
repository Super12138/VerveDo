package cn.super12138.todo.ui.widget.today

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.LazyListScope
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import cn.super12138.todo.R
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.logic.model.SortingMethod
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.widget.components.GlanceTaskCard
import cn.super12138.todo.ui.widget.components.GlanceTaskEmptyTip
import cn.super12138.todo.ui.widget.components.GlanceTitleBar
import cn.super12138.todo.utils.GlanceTypography
import cn.super12138.todo.utils.SystemUtils
import cn.super12138.todo.utils.sort
import cn.super12138.todo.utils.toInstant
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class TodayTaskWidget : GlanceAppWidget(), KoinComponent {
    val taskRepository: TaskRepository = get()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val scope = rememberCoroutineScope()
            val allTask by taskRepository.getAllTasks().collectAsState(emptyList())
            val todayTask = remember(allTask) {
                allTask
                    .filter {
                        if (it.dueDateMillis == null) return@filter false
                        it.dueDateMillis.toInstant() == SystemUtils.startOfUTCToday()
                    }
                    .sort(SortingMethod.Priority)
            }

            GlanceTheme {
                TodayTaskApp(
                    taskList = todayTask,
                    onChecked = { scope.launch { taskRepository.updateTask(it) } }
                )
            }
        }
    }
}

@Composable
private fun TodayTaskApp(
    taskList: List<TaskEntity>,
    modifier: GlanceModifier = GlanceModifier,
    onChecked: (TaskEntity) -> Unit = {}
) {
    val context = LocalContext.current
    val completeStr = remember(context) { context.getString(R.string.title_completed_task) }
    val incompleteStr = remember(context) { context.getString(R.string.label_task_incomplete) }
    val completeTasks = taskList.filter { it.isCompleted }
    val inCompleteTasks = taskList.filter { !it.isCompleted }

    Scaffold(
        titleBar = {
            val title = remember(taskList.size) {
                context.getString(R.string.label_widget_today_task)
            }
            val taskCount = remember(inCompleteTasks.size) {
                context.getString(
                    R.string.label_slot_item_task,
                    inCompleteTasks.size
                )
            }

            GlanceTitleBar(
                title = title,
                taskCount = taskCount,
                modifier = GlanceModifier.fillMaxWidth()
            )
        },
        horizontalPadding = VerveDoDefaults.screenHorizontalPadding,
        // Scaffold内部包含fillMaxSize Modifier
        modifier = modifier
    ) {
        if (taskList.isEmpty()) {
            GlanceTaskEmptyTip()
        } else {
            LazyColumn(
                modifier = GlanceModifier
                    .padding(top = VerveDoDefaults.contentPadding / 2)
                    .fillMaxSize()
            ) {
                if (inCompleteTasks.isNotEmpty()) {
                    title(incompleteStr)
                    items(
                        items = inCompleteTasks,
                        itemId = { task -> task.id.toLong() }
                    ) {
                        GlanceTaskCard(
                            content = it.content,
                            category = it.category,
                            dueDateMillis = it.dueDateMillis,
                            isCompleted = it.isCompleted,
                            showDueDate = false,
                            priority = Priority.fromFloat(it.priority),
                            onChecked = { onChecked(it.copy(isCompleted = true)) }
                        )
                    }
                }

                if (completeTasks.isNotEmpty()) {
                    title(completeStr)
                    items(
                        items = completeTasks,
                        itemId = { task -> task.id.toLong() }
                    ) {
                        GlanceTaskCard(
                            content = it.content,
                            category = it.category,
                            dueDateMillis = it.dueDateMillis,
                            isCompleted = it.isCompleted,
                            priority = Priority.fromFloat(it.priority),
                            showDueDate = false,
                            onChecked = { onChecked(it.copy(isCompleted = true)) }
                        )
                    }
                }
                item {
                    Spacer(GlanceModifier.size(VerveDoDefaults.contentPadding / 2))
                }
            }
        }
    }
}

private fun LazyListScope.title(title: String) {
    item {
        Spacer(GlanceModifier.size(VerveDoDefaults.contentPadding))
    }
    item {
        Text(
            text = title,
            style = GlanceTypography.labelLarge.copy(
                color = GlanceTheme.colors.primary,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1
        )
    }
}
