package cn.super12138.todo.ui.pages.editor

import android.content.Context
import android.util.Log
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
    private val context: Context,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    companion object {
        const val TAG = "Editor"
    }

    private val localUiState = MutableStateFlow(TaskEditorUiState())
    val uiState: StateFlow<TaskEditorUiState> = combine(
        dataStoreManager.textFieldAutoFocusFlow,
        dataStoreManager.categoriesFlow,
        localUiState
    ) { textFieldAutoFocus, categories, localState ->
        val categoryList = categories.mapIndexed { index, category ->
            ChipItem(
                id = index,
                name = category
            )
        } + ChipItem(
            id = -1,
            name = context.getString(R.string.label_customization)
        )

        localState.copy(
            isTextFieldAutoFocus = textFieldAutoFocus,
            categoryList = categoryList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
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
        localUiState.update {
            Log.d(TAG, "Selected category index: $index")
            it.copy(selectedCategoryIndex = index)
        }

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
            // 新建模式，清空一切
            it.copy(
                initialTask = null,
                contentState = TextFieldState(),
                categoryContentState = TextFieldState(),
                selectedCategoryIndex = if (uiState.value.categoryList.size - 1 >= 1) 0 else -1,
                priorityState = 0f,
                dueDateState = null,
                isCompleted = false
            )
        } else {
            // 编辑模式，填充任务数据
            val index =
                uiState.value.categoryList.firstOrNull { item -> item.name == task.category }?.id
                    ?: -1
            it.copy(
                initialTask = task,
                contentState = TextFieldState(task.content),
                categoryContentState = TextFieldState(task.category),
                selectedCategoryIndex = index,
                priorityState = task.priority,
                dueDateState = task.dueDate,
                isCompleted = task.isCompleted
            )
        }
    }
}