package cn.super12138.todo.ui.pages.tasks

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.ScreenMode
import cn.super12138.todo.utils.toLocalDateString

data class TasksPageUiState(
    private val originalTaskList: List<TaskEntity> = emptyList(),
    val taskListState: LazyListState = LazyListState(),
    val screenMode: ScreenMode = ScreenMode.Default,
    val selectedTaskIds: Set<Int> = emptySet(),
    val searchTextState: TextFieldState = TextFieldState(),
    val showDeleteConfirmDialog: Boolean = false
) {
    val taskList: List<TaskEntity>
        get() {
            val searchText = searchTextState.text.trim()
            if (searchText.isEmpty()) return originalTaskList // 搜索文本为空返回完整列表
            return originalTaskList.filter { task ->
                listOf(
                    task.content,
                    task.category,
                    task.dueDate?.toLocalDateString() ?: ""
                ).any { it.contains(searchText, ignoreCase = true) }
            }
        }

    val isInSelectionMode: Boolean
        get() = screenMode == ScreenMode.Selection

    val isInSearchMode: Boolean
        get() = screenMode == ScreenMode.Search
}