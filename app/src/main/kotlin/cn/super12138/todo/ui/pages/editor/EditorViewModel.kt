package cn.super12138.todo.ui.pages.editor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.R
import cn.super12138.todo.logic.IRepository
import cn.super12138.todo.logic.Repository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.ui.components.ChipItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditorViewModel(
    private val initialTask: TaskEntity? = null,
    private val context: Context,
    private val repository: IRepository,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskEditorUiState())
    val uiState: StateFlow<TaskEditorUiState> = _uiState

    init {
        dataStoreManager.textFieldAutoFocusFlow
            .combine(
                dataStoreManager.categoriesFlow,
                transform = { textFieldAutoFocus, categories ->
                    Pair(textFieldAutoFocus, categories)
                }
            )
            .onEach { (textFieldAutoFocus, categories) ->
                val categoryList = categories.mapIndexed { index, category ->
                    ChipItem(
                        id = index,
                        name = category
                    )
                } + ChipItem(
                    id = -1,
                    name = context.getString(R.string.label_customization)
                )

                // TODO: 逻辑、可行性验证，会不会出现无法选中分类的现象
                val initialCategoryId = if (initialTask == null) {
                    if (categoryList.size == 1) -1 else 0
                } else {
                    categoryList.firstOrNull { it.name == initialTask.category }?.id ?: -1
                }

                _uiState.update {
                    it.copy(
                        isTextFieldAutoFocus = textFieldAutoFocus,
                        categoryList = categoryList,
                        selectedCategoryIndex = initialCategoryId
                    )
                }
            }
            .launchIn(viewModelScope)
    }

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

    fun setPriority(priority: Float) = _uiState.update { it.copy(priorityState = priority) }
    fun setDueDate(dueDate: Long?) = _uiState.update { it.copy(dueDateState = dueDate) }
    fun setCompleted(isCompleted: Boolean) = _uiState.update { it.copy(isCompleted = isCompleted) }

    fun showDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun hideDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = false) }

    fun showExitConfirmDialog() = _uiState.update { it.copy(showExitConfirmDialog = true) }
    fun hideExitConfirmDialog() = _uiState.update { it.copy(showExitConfirmDialog = false) }
    fun setSelectedCategory(index: Int) = _uiState.update { it.copy(selectedCategoryIndex = index) }
    fun clearError() = _uiState.update {
        it.copy(
            isContentError = false,
            isCategoryError = false
        )
    }

    fun setErrorIfNotValid(): Boolean {
        val contentError = !uiState.value.isContentValid()
        val categoryError = !uiState.value.isCategoryValid()
        val hasError = contentError || categoryError

        _uiState.update {
            it.copy(
                isContentError = contentError,
                isCategoryError = categoryError
            )
        }

        return hasError
    }
}