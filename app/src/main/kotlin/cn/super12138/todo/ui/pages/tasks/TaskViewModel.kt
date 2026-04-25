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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
    private val _uiState = MutableStateFlow(TasksPageUiState())
    val uiState: StateFlow<TasksPageUiState> = _uiState

    init {
        val taskList: Flow<List<TaskEntity>> = repository.getAllTasks()
        // 业务状态流合并
        dataStoreManager.sortingMethodFlow
            .flatMapLatest { method -> taskList.map { it.sort(SortingMethod.fromId(method)) } }
            .onEach { sortedList ->
                _uiState.update { it.copy(originalTaskList = sortedList) }
            }
            .launchIn(viewModelScope)
    }

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
        _uiState.update {
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
        _uiState.update { it.copy(selectedTaskIds = allIds) }
    }

    /**
     * 清除全部已选择的待办
     */
    fun clearAllTaskSelection() = _uiState.update { it.copy(selectedTaskIds = emptySet()) }

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
        _uiState.update { it.copy(selectedTaskIds = setOf(id), screenMode = ScreenMode.Selection) }

    fun exitMultiSelectMode() =
        _uiState.update { it.copy(selectedTaskIds = emptySet(), screenMode = ScreenMode.Default) }

    fun enterSearchMode() = _uiState.update { it.copy(screenMode = ScreenMode.Search) }
    fun exitSearchMode() = _uiState.update { it.copy(screenMode = ScreenMode.Default) }
    fun showDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun hideDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = false) }
}