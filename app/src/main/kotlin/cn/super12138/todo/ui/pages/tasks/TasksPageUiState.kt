package cn.super12138.todo.ui.pages.tasks

import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.SortingOption
import cn.super12138.todo.logic.model.SortingOrder

data class TasksPageUiState(
    val originalTaskList: List<TaskEntity> = emptyList(),
    val selectedTaskIds: Set<Int> = emptySet(),
    val inSearchMode: Boolean = false,
    val inSelectionMode: Boolean = false,
    val sortingOption: SortingOption = SortingOption.Sequential,
    val sortingOrder: SortingOrder = SortingOrder.Ascending,
    val searchQuery: String = "",
    val showDeleteConfirmDialog: Boolean = false
)