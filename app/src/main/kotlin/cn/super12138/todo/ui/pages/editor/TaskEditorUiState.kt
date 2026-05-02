package cn.super12138.todo.ui.pages.editor

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.ui.components.ChipItem

data class TaskEditorUiState(
    val initialTask: TaskEntity? = null,
    val contentState: TextFieldState = TextFieldState(initialTask?.content ?: ""),
    val categoryContentState: TextFieldState = TextFieldState(initialTask?.category ?: ""),
    val selectedCategoryIndex: Int = -1,
    val priorityState: Float = initialTask?.priority ?: 0f,
    val dueDateState: Long? = initialTask?.dueDate,
    val isCompleted: Boolean = initialTask?.isCompleted == true,
    val categoryList: List<ChipItem> = emptyList(),
    val isTextFieldAutoFocus: Boolean = false, // 数据来源于应用设置
    val isContentError: Boolean = false,
    val isCategoryError: Boolean = false,
    val showExitConfirmDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false
) {
    fun isContentValid(): Boolean = contentState.text.trim().isNotEmpty()

    fun isCategoryValid(): Boolean =
        !(selectedCategoryIndex == -1 && categoryContentState.text.trim().isEmpty())

    fun isValid(): Boolean = isContentValid() && isCategoryValid()

    fun isModified(): Boolean {
        Log.d(
            "Editor",
            "UiState: Original: content=${initialTask?.content}, category=${initialTask?.category}, priority=${initialTask?.priority}, isCompleted=${initialTask?.isCompleted}, dueDate=${initialTask?.dueDate}"
        )
        Log.d(
            "Editor",
            "UiState: Current: content=${contentState.text}, category=${categoryContentState.text}, priority=${priorityState}, isCompleted=${isCompleted}, dueDate=${dueDateState}"
        )
        var isModified = false
        if ((initialTask?.content ?: "") != contentState.text.toString()) isModified = true
        if ((initialTask?.category ?: "") != categoryContentState.text.toString()) isModified = true
        if ((initialTask?.priority ?: 0f) != priorityState) isModified = true
        if ((initialTask?.isCompleted == true) != isCompleted) isModified = true
        if (initialTask?.dueDate != dueDateState) isModified = true
        return isModified
    }

    /*fun getNewTaskEntity(): TaskEntity {
        return TaskEntity(
            id = initialTask?.id ?: 0,
            content = taskContentState.text.toString(),
            category = categoryContentState.text.toString(),
            priority = priorityState,
            isCompleted = isCompleted,
            dueDate = dueDateState
        )
    }*/
}
