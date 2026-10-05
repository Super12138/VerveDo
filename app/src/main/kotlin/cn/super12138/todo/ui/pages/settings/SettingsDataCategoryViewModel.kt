package cn.super12138.todo.ui.pages.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsDataCategoryViewModel(private val settingsRepository: SettingsRepository) :
    ViewModel() {
    val localUiState = MutableStateFlow(SettingsDataCategoryUiState())
    val uiState: StateFlow<SettingsDataCategoryUiState> =
        combine(
        settingsRepository.categoriesFlow,
        localUiState
        ) { categories, localUiState -> localUiState.copy(categories = categories) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SettingsDataCategoryUiState()
            )

    fun setEditingCategory(value: String) = localUiState.update { it.copy(editingCategory = value) }
    fun addCategory(new: String) {
        val old = uiState.value.editingCategory
        val oldList = uiState.value.categories

        val list = if (old.isEmpty()) {
            if (oldList.contains(new)) {
                // 调换分类位置
                oldList - new + new
            } else {
                oldList + new
            }
        } else {
            if (old == new) {
                oldList
            } else {
                if (oldList.contains(new)) {
                    oldList - old
                } else {
                    oldList - old + new
                }
            }
        }

        viewModelScope.launch {
            settingsRepository.setCategories(list)
        }
    }

    fun removeCategory(category: String) {
        viewModelScope.launch {
            settingsRepository.setCategories(uiState.value.categories - category)
        }
    }

    fun showAddDialog() = localUiState.update { it.copy(showAddDialog = true) }
    fun hideAddDialog() = localUiState.update { it.copy(showAddDialog = false) }
}