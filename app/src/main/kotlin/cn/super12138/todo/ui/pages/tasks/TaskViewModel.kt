package cn.super12138.todo.ui.pages.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.IRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.ScreenMode
import cn.super12138.todo.logic.model.SortingMethod
import cn.super12138.todo.utils.sort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*.stateIn(
scope = viewModelScope,
started = SharingStarted.WhileSubscribed(5000),
initialValue = emptyList()
)*/
@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val repository: IRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val localUiState = MutableStateFlow(TasksPageUiState())
    val uiState: StateFlow<TasksPageUiState> = combine(
        repository.getAllTasks(),
        dataStoreManager.sortingMethodFlow,
        localUiState
    ) { taskList, sortingMethod, localUiState ->
        val sortedList = taskList.sort(SortingMethod.fromId(sortingMethod))
        localUiState.copy(originalTaskList = sortedList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksPageUiState()
    )

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    /*fun deleteAllTodo() {
        viewModelScope.launch {
            Repository.deleteAllTodo()
        }
    }*/

    /**
     * 切换待办的选择状态
     */
    fun toggleTaskSelection(task: TaskEntity) {
        localUiState.update {
            val newIds = if (it.selectedTaskIds.contains(task.id)) { // 已选择的Id里包含切换选择状态的Id
                it.selectedTaskIds - task.id // 那么就给他删了
            } else {
                it.selectedTaskIds + task.id // 不然给他加上
            }
            val newMode = if (newIds.isEmpty()) {
                // 如果之前是搜索模式，回到搜索模式，否则回到普通模式
                if (uiState.value.searchTextState.text.isNotEmpty()) ScreenMode.Search else ScreenMode.Default
            } else {
                ScreenMode.Selection
            }
            it.copy(selectedTaskIds = newIds, screenMode = newMode)
        }
    }

    /**
     * 切换是否全选
     */
    fun selectAllTask() {
        val allIds = uiState.value.taskList.map { it.id }.toSet()
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
            repository.deleteTaskFromIds(uiState.value.selectedTaskIds)
            clearAllTaskSelection()
        }
    }

    fun enterMultiSelectMode(id: Int) =
        localUiState.update {
            it.copy(
                selectedTaskIds = setOf(id),
                screenMode = ScreenMode.Selection
            )
        }

    fun exitMultiSelectMode() =
        localUiState.update {
            it.copy(
                selectedTaskIds = emptySet(),
                screenMode = ScreenMode.Default
            )
        }

    fun enterSearchMode() = localUiState.update { it.copy(screenMode = ScreenMode.Search) }
    fun exitSearchMode() = localUiState.update { it.copy(screenMode = ScreenMode.Default) }
    fun showDeleteConfirmDialog() = localUiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun hideDeleteConfirmDialog() = localUiState.update { it.copy(showDeleteConfirmDialog = false) }
}