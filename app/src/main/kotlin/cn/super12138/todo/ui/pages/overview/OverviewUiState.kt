package cn.super12138.todo.ui.pages.overview

import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.DarkMode

data class OverviewUiState(
    val darkMode: DarkMode = DarkMode.FollowSystem,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val todayTasks: List<TaskEntity> = emptyList(),
    val nextWeekTasks: List<TaskEntity> = emptyList(),
    val pinnedTasks: List<TaskEntity> = emptyList()
)
