package cn.super12138.todo.ui.widget.all

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
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import cn.super12138.todo.R
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.logic.model.SortingMethod
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.widget.components.GlanceTaskCard
import cn.super12138.todo.ui.widget.components.GlanceTaskEmptyTip
import cn.super12138.todo.ui.widget.components.GlanceTitleBar
import cn.super12138.todo.utils.sort
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AllIncompleteWidget : GlanceAppWidget(), KoinComponent {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val taskRepository: TaskRepository = get()

        provideContent {
            val scope = rememberCoroutineScope()
            val allTask by taskRepository.getAllTasks().collectAsState(emptyList())
            val allIncompleteTask = remember(allTask) {
                allTask.filter { !it.isCompleted }.sort(SortingMethod.Priority)
            }

            GlanceTheme {
                TaskWidgetApp(
                    taskList = allIncompleteTask,
                    onChecked = { scope.launch { taskRepository.updateTask(it) } }
                )
            }
        }
    }
}

@Composable
private fun TaskWidgetApp(
    taskList: List<TaskEntity>,
    modifier: GlanceModifier = GlanceModifier,
    onChecked: (TaskEntity) -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        titleBar = {
            val title = remember(taskList.size) {
                context.getString(R.string.label_task_incomplete)
            }
            val taskCount = remember(taskList.size) {
                context.getString(
                    R.string.label_slot_item_task,
                    taskList.size
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
                items(items = taskList, itemId = { task -> task.id.toLong() }) {
                    GlanceTaskCard(
                        content = it.content,
                        category = it.category,
                        dueDateInstant = it.dueDateInstant,
                        isCompleted = it.isCompleted,
                        priority = Priority.fromFloat(it.priority),
                        onChecked = { onChecked(it.copy(isCompleted = true)) }
                    )
                }
                item {
                    Spacer(GlanceModifier.size(VerveDoDefaults.contentPadding / 2))
                }
            }
        }
    }
}
