package cn.super12138.todo.ui.pages.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.SettingsRepository
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.logic.model.SortingDirection
import cn.super12138.todo.logic.model.SortingOption
import cn.super12138.todo.utils.SystemUtils
import cn.super12138.todo.utils.sortTaskBy
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.days

class OverviewViewModel(
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<OverviewUiState> =
        combine(
            taskRepository.getAllTasks(),
            settingsRepository.darkModeFlow
        ) { taskEntities, darkMode ->
            val total = taskEntities.size
            val completed = taskEntities.count { task -> task.isCompleted }
            val pending = total - completed

            val today = SystemUtils.startOfUTCToday()

            val todayTasks = taskEntities.filter { task ->
                if (task.dueDate == null) return@filter false
                task.dueDate == today // 判断截止日期是否为今天
            }

            val nextWeekTasks = taskEntities.filter { task -> // 先过滤
                if (task.dueDate == null) return@filter false
                // 截止日期是否在今天到一周之后并且未完成
                task.dueDate in today..(today + 7.days) && !task.isCompleted
            }.sortTaskBy(
                SortingOption.DueDate to SortingDirection.Ascending,
                SortingOption.Priority to SortingDirection.Descending
            )

            val pinnedTasks = taskEntities.filter { it.isPinned }

            OverviewUiState(
                darkMode = darkMode,
                totalTasks = total,
                completedTasks = completed,
                pendingTasks = pending,
                todayTasks = todayTasks,
                nextWeekTasks = nextWeekTasks,
                pinnedTasks = pinnedTasks
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = OverviewUiState()
            )
}