package cn.super12138.todo.ui.pages.overview

import cn.super12138.todo.logic.database.TaskEntity

data class OverviewUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val todayTasks: List<TaskEntity> = emptyList(),
    val nextWeekTasks: List<TaskEntity> = emptyList()
)
