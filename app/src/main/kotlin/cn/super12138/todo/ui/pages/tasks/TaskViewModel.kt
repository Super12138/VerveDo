package cn.super12138.todo.ui.pages.tasks

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.IRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.SortingMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: IRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val taskList: Flow<List<TaskEntity>> = repository.getAllTasks()
    val sortedTaskList: StateFlow<List<TaskEntity>> = dataStoreManager.sortingMethodFlow
        .flatMapLatest { sortingMethod ->
            taskList.map { list ->
                when (SortingMethod.Companion.fromId(sortingMethod)) {
                    SortingMethod.Sequential -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted } // 必须先要按照是否完成排序
                            .thenBy { it.id }
                    )

                    SortingMethod.Category -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenBy { it.category }
                    )

                    SortingMethod.Priority -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenByDescending { it.priority }
                            .thenBy(nullsLast()) { it.dueDate }
                    ) // 优先级高的在前

                    SortingMethod.Completion -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenBy { it.category }
                            .thenByDescending { it.priority }
                    ) // 未完成的在前
                    SortingMethod.AlphabeticalAscending -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenBy { it.content }
                            .thenByDescending { it.priority }
                    )

                    SortingMethod.AlphabeticalDescending -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenByDescending { it.content }
                            .thenByDescending { it.priority }
                    )

                    SortingMethod.DueDate -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            // 确保未设置截止日期的任务在最下头
                            .thenBy(nullsLast()) { it.dueDate }
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val taskListState = LazyListState()
    var searchMode by mutableStateOf(false)
        private set
    val searchFieldState = TextFieldState()

    // 多选逻辑参考：https://github.com/X1nto/Mauth
    private val _selectedTodoIds = MutableStateFlow(listOf<Int>())
    val selectedTodoIds = _selectedTodoIds.asStateFlow()

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
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
        _selectedTodoIds.update { idList ->
            if (idList.contains(task.id)) {
                // 若已经选择取消选择
                idList - task.id
            } else {
                // 若未选择添加到列表中，立即选中
                idList + task.id
            }
        }
    }

    /**
     * 切换是否全选
     */
    fun selectAllTask() {
        viewModelScope.launch {
            taskList.firstOrNull()?.let { tasks ->
                // 无论是否有选择都全选
                val allIds = tasks.map { it.id }
                _selectedTodoIds.value = allIds
            }
        }
    }

    /**
     * 清除全部已选择的待办
     */
    fun clearAllTaskSelection() {
        _selectedTodoIds.update { emptyList() }
    }

    /**
     * 删除选择的待办
     */
    fun deleteSelectedTask() {
        viewModelScope.launch {
            repository.deleteTaskFromIds(selectedTodoIds.value)
            clearAllTaskSelection()
        }
    }


    fun setSearchModeEnabled(enabled: Boolean) {
        searchMode = enabled
    }
}