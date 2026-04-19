package cn.super12138.todo.ui.pages.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.IRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.utils.SystemUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OverviewViewModel(
    private val repository: IRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.getAllTasks().collect {
                val total = it.size
                val completed = it.count { task -> task.isCompleted }
                val pending = total - completed

                val todayMillis = SystemUtils.getTodayEightAM()
                val dayMillis = 24L * 60 * 60 * 1000

                val todayTasks = it.filter { task ->
                    val due = task.dueDate ?: return@filter false // 如果截止日期为空立即返回null
                    due == todayMillis // 判断截止日期是否为今天
                }

                val nextWeekTasks = it.filter { task -> // 先过滤
                    val due = task.dueDate ?: return@filter false
                    // 截止日期是否在今天到一周之后并且未完成
                    due in todayMillis..(todayMillis + 7 * dayMillis) && !task.isCompleted
                }.sortedWith( // 后排序
                    comparator = compareBy<TaskEntity> { it.dueDate } // 截止日期近的靠前
                        .thenBy { it.category } // TODO：可选删了
                        .thenByDescending { it.priority } // 优先级高的靠前
                )

                _uiState.value = OverviewUiState(
                    totalTasks = total,
                    completedTasks = completed,
                    pendingTasks = pending,
                    todayTasks = todayTasks,
                    nextWeekTasks = nextWeekTasks
                )
            }
        }
    }
}