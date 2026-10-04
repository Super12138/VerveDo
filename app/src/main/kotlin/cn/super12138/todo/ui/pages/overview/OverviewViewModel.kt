package cn.super12138.todo.ui.pages.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.utils.SystemUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.days

class OverviewViewModel(private val taskRepository: TaskRepository) : ViewModel() {
    val uiState: StateFlow<OverviewUiState> = taskRepository.getAllTasks()
        .map {
            val total = it.size
            val completed = it.count { task -> task.isCompleted }
            val pending = total - completed

            val today = SystemUtils.startOfUTCToday()

            val todayTasks = it.filter { task ->
                if (task.dueDate == null) return@filter false
                task.dueDate == today // 判断截止日期是否为今天
            }

            val nextWeekTasks = it.filter { task -> // 先过滤
                if (task.dueDate == null) return@filter false
                // 截止日期是否在今天到一周之后并且未完成
                task.dueDate in today..(today + 7.days) && !task.isCompleted
            }.sortedWith( // 后排序
                comparator = compareBy<TaskEntity> { it.dueDate } // 截止日期近的靠前
                    .thenBy { it.category } // TODO：可选删了
                    .thenByDescending { it.priority } // 优先级高的靠前
            )

            val pinnedTasks = it.filter { it.isPinned }

            OverviewUiState(
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