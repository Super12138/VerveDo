package cn.super12138.todo.ui.pages.editor

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.R
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.ui.components.ChipItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class EditorViewModel(
    private val initialTask: TaskEntity? = null,
    private val context: Context,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    private val localUiState = MutableStateFlow(TaskEditorUiState())
    val uiState: StateFlow<TaskEditorUiState> = combine(
        dataStoreManager.textFieldAutoFocusFlow,
        dataStoreManager.categoriesFlow,
        localUiState
    ) { textFieldAutoFocus, categories, localUiState ->
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

        localUiState.copy(
            isTextFieldAutoFocus = textFieldAutoFocus,
            categoryList = categoryList,
            selectedCategoryIndex = initialCategoryId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskEditorUiState()
    )

    fun setPriority(priority: Float) = localUiState.update { it.copy(priorityState = priority) }
    fun setDueDate(dueDate: Long?) = localUiState.update { it.copy(dueDateState = dueDate) }
    fun setCompleted(isCompleted: Boolean) =
        localUiState.update { it.copy(isCompleted = isCompleted) }

    fun showDeleteConfirmDialog() = localUiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun hideDeleteConfirmDialog() = localUiState.update { it.copy(showDeleteConfirmDialog = false) }

    fun showExitConfirmDialog() = localUiState.update { it.copy(showExitConfirmDialog = true) }
    fun hideExitConfirmDialog() = localUiState.update { it.copy(showExitConfirmDialog = false) }
    fun setSelectedCategory(index: Int) =
        localUiState.update { it.copy(selectedCategoryIndex = index) }

    fun clearError() = localUiState.update {
        it.copy(
            isContentError = false,
            isCategoryError = false
        )
    }

    fun setErrorIfNotValid(): Boolean {
        val contentError = !uiState.value.isContentValid()
        val categoryError = !uiState.value.isCategoryValid()
        val hasError = contentError || categoryError

        localUiState.update {
            it.copy(
                isContentError = contentError,
                isCategoryError = categoryError
            )
        }

        return hasError
    }

    fun setTaskEntity(task: TaskEntity?) = localUiState.update {
        if (task == null) {
            it.copy(
                initialTask = null,
                taskContentState = TextFieldState(),
                categoryContentState = TextFieldState(),
                priorityState = 0f,
                dueDateState = null,
                isCompleted = false
            )
        } else {
            it.copy(
                initialTask = task,
                taskContentState = TextFieldState(task.content),
                categoryContentState = TextFieldState(task.category),
                priorityState = task.priority,
                dueDateState = task.dueDate,
                isCompleted = task.isCompleted
            )
        }
    }
}