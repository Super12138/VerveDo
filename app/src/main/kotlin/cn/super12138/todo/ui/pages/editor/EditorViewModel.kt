package cn.super12138.todo.ui.pages.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.SettingsRepository
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.components.ChipItem
import cn.super12138.todo.utils.ConfettiController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant

class EditorViewModel(
    val initialTask: TaskEntity?,
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository,
    private val confettiController: ConfettiController
) : ViewModel() {
    private val localUiState = MutableStateFlow(TaskEditorUiState())
    val uiState: StateFlow<TaskEditorUiState> = combine(
        settingsRepository.textFieldAutoFocusFlow,
        settingsRepository.categoriesFlow,
        localUiState
    ) { textFieldAutoFocus, categories, localState ->
        localState.copy(
            shouldAutoFocusContent = textFieldAutoFocus,
            categoryList = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskEditorUiState()
    )

    private var initialCategory = ""

    init {
        if (initialTask != null) {
            with(initialTask) {
                localUiState.update {
                    it.copy(
                        content = content,
                        category = category,
                        priority = Priority.fromFloat(priority),
                        dueDateInstant = dueDateInstant,
                        isCompleted = isCompleted
                    )
                }
            }
        }
    }

    fun setContentText(content: String) = localUiState.update { it.copy(content = content) }
    fun setCategoryText(category: String) = localUiState.update { it.copy(category = category) }
    fun setPriority(priority: Priority) = localUiState.update { it.copy(priority = priority) }
    fun setDueDate(dueDate: Instant?) = localUiState.update { it.copy(dueDateInstant = dueDate) }
    fun setCompleted(completed: Boolean) = localUiState.update { it.copy(isCompleted = completed) }
    fun isModified(): Boolean {
        var isModified = false

        with(uiState.value) {
            if ((initialTask?.content ?: "") != content.trim()) isModified = true
            if ((initialTask?.category ?: initialCategory) != category.trim()) isModified = true
            if ((initialTask?.priority ?: 0f) != priority.value) isModified = true
            if ((initialTask?.isCompleted == true) != isCompleted) isModified = true
            if (initialTask?.dueDateInstant != dueDateInstant) isModified = true
        }

        return isModified
    }


    fun showDeleteConfirmDialog() = localUiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun showExitConfirmDialog() = localUiState.update { it.copy(showExitConfirmDialog = true) }
    fun hideDeleteConfirmDialog() = localUiState.update { it.copy(showDeleteConfirmDialog = false) }
    fun hideExitConfirmDialog() = localUiState.update { it.copy(showExitConfirmDialog = false) }
    fun setInitialCategory(chipItem: ChipItem?) {
        setSelectedCategory(chipItem)
        initialCategory = chipItem?.label ?: ""
    }

    fun setSelectedCategory(chipItem: ChipItem?) {
        if (chipItem == null) return
        localUiState.update {
            it.copy(
                selectedCategoryId = chipItem.id,
                category = if (chipItem.id == -1) it.category else chipItem.label
            )
        }
    }

    fun saveNewTask() {
        if (!isModified()) return
        val task = with(uiState.value) {
            TaskEntity(
                content = content,
                category = category,
                isCompleted = isCompleted,
                priority = priority.value,
                dueDateInstant = dueDateInstant,
                id = initialTask?.id ?: 0
            )
        }
        viewModelScope.launch { taskRepository.insertTask(task) }
    }

    fun deleteTask() {
        if (initialTask == null) return
        viewModelScope.launch { taskRepository.deleteTask(initialTask) }
    }

    fun setConfettiVisibility(visible: Boolean) = confettiController.setVisibility(visible)
}