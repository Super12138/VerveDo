package cn.super12138.todo.ui.pages.editor.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cn.super12138.todo.R
import cn.super12138.todo.logic.database.TodoEntity

class EditorState(val initialTodo: TodoEntity? = null) {
    /* companion object{
         fun Saver() =
             androidx.compose.runtime.saveable.Saver<EditorState, TodoEntity>(
                 save = { it.initialTodo },
                 restore = { EditorState(it) },
             )
     }*/
    var toDoContent by mutableStateOf(initialTodo?.content ?: "")
    var isErrorContent by mutableStateOf(false)
    var selectedCategoryIndex by mutableIntStateOf(-1)
    var categoryContent by mutableStateOf(initialTodo?.category ?: "")
    var isErrorCategory by mutableStateOf(false)
    var priorityState by mutableFloatStateOf(initialTodo?.priority ?: 0f)
    var dueDateState by mutableStateOf(initialTodo?.dueDate)
    var isCompleted by mutableStateOf(initialTodo?.isCompleted == true)
    var categorySupportingText by mutableIntStateOf(R.string.tip_short_category)
        private set

    var showExitConfirmDialog by mutableStateOf(false)
    var showDeleteConfirmDialog by mutableStateOf(false)

    /**
     * 检查待办内容和学科的一者是否无有效，如果无效则为文本框设置错误状态
     *
     * @return 它们二者是否都有效。有一者无效就返回 true
     */
    fun setErrorIfNotValid(): Boolean {
        isErrorContent = toDoContent.trim().isEmpty()
        if (selectedCategoryIndex == -1 && categoryContent.trim().isEmpty()) {
            isErrorCategory = true
            categorySupportingText = R.string.error_no_content_entered
        } else {
            isErrorCategory = false
        }
        return isErrorContent || isErrorCategory
    }

    /**
     * 清除文本框全部错误
     */
    fun clearError() {
        isErrorContent = false
        isErrorCategory = false
    }

    /**
     * 检查待办是否被编辑修改
     */
    fun isModified(): Boolean {
        // Log.d("EditorState", "Initial: content='${initialTodo?.content ?: ""}', category='${initialTodo?.category ?: ""}', priority=${initialTodo?.priority ?: 0f}, isCompleted=${initialTodo?.isCompleted == true}, dueDate=${initialTodo?.dueDate} ; Now: content='$toDoContent', category='$categoryContent', priority=$priorityState, isCompleted=$isCompleted, dueDate=$dueDateState")
        var isModified = false
        if ((initialTodo?.content ?: "") != toDoContent) isModified = true
        if ((initialTodo?.category ?: "") != categoryContent) isModified = true
        if ((initialTodo?.priority ?: 0f) != priorityState) isModified = true
        if ((initialTodo?.isCompleted == true) != isCompleted) isModified = true
        if (initialTodo?.dueDate != dueDateState) isModified = true
        return isModified
    }

    /**
     * 保存状态的 Saver 对象，用于适配 rememberSaveable
     */
    object Saver : androidx.compose.runtime.saveable.Saver<EditorState, Any> {
        override fun SaverScope.save(value: EditorState): Any {
            return listOf(
                // 避免错误：java.lang.RuntimeException: Parcel: unable to marshal value TodoEntity(...)
                value.initialTodo?.let {
                    listOf(
                        it.content,
                        it.category,
                        it.isCompleted,
                        it.priority,
                        it.dueDate,
                        it.id
                    )
                },
                value.toDoContent,
                value.isErrorContent,
                value.selectedCategoryIndex,
                value.categoryContent,
                value.isErrorCategory,
                value.priorityState,
                value.dueDateState,
                value.isCompleted,
                value.showExitConfirmDialog,
                value.showDeleteConfirmDialog
            )
        }

        override fun restore(value: Any): EditorState {
            val list = value as List<*>
            val initialTodoList = list[0] as? List<*>
            val initialTodo = initialTodoList?.let {
                TodoEntity(
                    content = it[0] as String,
                    category = it[1] as String,
                    isCompleted = it[2] as Boolean,
                    priority = it[3] as Float,
                    dueDate = it[4] as Long?,
                    id = it[5] as Int
                )
            }
            return EditorState(initialTodo).apply {
                toDoContent = list[1] as String
                isErrorContent = list[2] as Boolean
                selectedCategoryIndex = list[3] as Int
                categoryContent = list[4] as String
                isErrorCategory = list[5] as Boolean
                priorityState = list[6] as Float
                dueDateState = list[7] as Long?
                isCompleted = list[8] as Boolean
                showExitConfirmDialog = list[9] as Boolean
                showDeleteConfirmDialog = list[10] as Boolean
            }
        }
    }
}

@Composable
fun rememberEditorState(initialTodo: TodoEntity? = null): EditorState =
    rememberSaveable(saver = EditorState.Saver) { EditorState(initialTodo) }