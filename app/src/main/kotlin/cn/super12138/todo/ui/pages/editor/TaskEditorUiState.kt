package cn.super12138.todo.ui.pages.editor

import cn.super12138.todo.logic.model.Priority
import kotlin.time.Instant

data class TaskEditorUiState(
    val content: String = "",
    val category: String = "",
    val priority: Priority = Priority.Default,
    val dueDateInstant: Instant? = null,
    val isCompleted: Boolean = false,
    val selectedCategoryId: Int = -1,
    val categoryList: List<String> = emptyList(),
    val shouldAutoFocusContent: Boolean = false,
    val showExitConfirmDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false
)
