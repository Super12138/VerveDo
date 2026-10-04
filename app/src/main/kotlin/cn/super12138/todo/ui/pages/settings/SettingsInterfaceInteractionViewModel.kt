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

class SettingsInterfaceInteractionViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val localUiState = MutableStateFlow(SettingsInterfaceUiState())
    val interfaceUiState: StateFlow<SettingsInterfaceUiState> = combine(
        settingsRepository.textFieldAutoFocusFlow,
        settingsRepository.secureModeFlow,
        settingsRepository.hapticFeedbackFlow,
        localUiState
    ) { textFieldAutoFocus, secureMode, hapticFeedback, localUiState ->
        localUiState.copy(
            textFieldAutoFocus = textFieldAutoFocus,
            secureMode = secureMode,
            hapticFeedback = hapticFeedback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsInterfaceUiState()
    )

    fun setTextFieldAutoFocus(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setTextFieldAutoFocus(value)
        }
    }

    fun setSecureMode(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSecureMode(value)
        }
    }

    fun setHapticFeedback(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHapticFeedback(value)
        }
    }

    fun showSortingMethodDialog() = localUiState.update { it.copy(showSortingMethodDialog = true) }
    fun hideSortingMethodDialog() = localUiState.update { it.copy(showSortingMethodDialog = false) }
}