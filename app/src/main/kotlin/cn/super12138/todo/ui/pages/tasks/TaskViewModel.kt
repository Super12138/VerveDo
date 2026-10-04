package cn.super12138.todo.ui.pages.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.SettingsRepository
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.SortingOption
import cn.super12138.todo.logic.model.SortingOrder
import cn.super12138.todo.utils.ConfettiController
import cn.super12138.todo.utils.sortTaskBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository,
    private val confettiController: ConfettiController
) : ViewModel() {
    private val localUiState = MutableStateFlow(TasksPageUiState())
    val uiState: StateFlow<TasksPageUiState> = combine(
        taskRepository.getAllTasks(),
        settingsRepository.sortingOptionFlow,
        settingsRepository.sortingOrderFlow,
        localUiState
    ) { taskList, sortingOption, sortingOrder, localUiState ->
        val sortedList = taskList.sortTaskBy(sortingOption, sortingOrder)
        localUiState.copy(
            originalTaskList = sortedList,
            sortingOption = sortingOption,
            sortingOrder = sortingOrder
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksPageUiState()
    )

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    /**
     * 切换待办的选择状态并设置相应的屏幕模式
     */
    fun toggleTaskSelection(task: TaskEntity) {
        localUiState.update {
            val newIds = if (it.selectedTaskIds.contains(task.id)) { // 已选择的Id里包含切换选择状态的Id
                it.selectedTaskIds - task.id // 那么就给他删了
            } else {
                it.selectedTaskIds + task.id // 不然给他加上
            }
            it.copy(selectedTaskIds = newIds, inSelectionMode = newIds.isNotEmpty())
        }
    }

    /**
     * 切换是否全选
     */
    fun selectVisibleAllTask(taskList: List<TaskEntity>) {
        val allIds = taskList.map { it.id }.toSet()
        localUiState.update { it.copy(selectedTaskIds = allIds) }
    }

    /**
     * 清除全部已选择的待办
     */
    fun clearAllTaskSelection() = localUiState.update { it.copy(selectedTaskIds = emptySet()) }

    /**
     * 删除选择的待办
     */
    fun deleteSelectedTask() {
        viewModelScope.launch {
            taskRepository.deleteTaskFromIds(uiState.value.selectedTaskIds)
            clearAllTaskSelection()
        }
    }

    fun enterMultiSelectMode(id: Int) =
        localUiState.update {
            it.copy(
                selectedTaskIds = setOf(id),
                inSelectionMode = true
            )
        }

    fun exitMultiSelectMode() =
        localUiState.update {
            it.copy(
                selectedTaskIds = emptySet(),
                inSelectionMode = false
            )
        }

    fun enterSearchMode() = localUiState.update { it.copy(inSearchMode = true) }
    fun exitSearchMode() = localUiState.update { it.copy(searchQuery = "", inSearchMode = false) }
    fun setSortingOption(option: SortingOption) =
        viewModelScope.launch { settingsRepository.setSortingOption(option) }

    fun setSortingOrder(order: SortingOrder) =
        viewModelScope.launch { settingsRepository.setSortingOrder(order) }

    fun showDeleteConfirmDialog() = localUiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun hideDeleteConfirmDialog() = localUiState.update { it.copy(showDeleteConfirmDialog = false) }

    fun updateSearchQuery(query: String) = localUiState.update { it.copy(searchQuery = query) }

    fun setConfettiVisibility(visible: Boolean) = confettiController.setVisibility(visible)
}