package cn.super12138.todo.ui.pages.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.SettingsRepository
import cn.super12138.todo.ui.navigation.VerveDoDestinations
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
    val uiState: StateFlow<SettingsInterfaceUiState> = combine(
        settingsRepository.textFieldAutoFocusFlow,
        settingsRepository.secureModeFlow,
        settingsRepository.hapticFeedbackFlow,
        settingsRepository.startupPage,
        localUiState
    ) { textFieldAutoFocus, secureMode, hapticFeedback, startupPage, localUiState ->
        localUiState.copy(
            startupPage = startupPage,
            textFieldAutoFocus = textFieldAutoFocus,
            secureMode = secureMode,
            hapticFeedback = hapticFeedback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsInterfaceUiState()
    )

    fun setStartupPage(value: VerveDoDestinations) {
        viewModelScope.launch {
            settingsRepository.setStartupPage(value)
        }
    }

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

    fun showDialog() = localUiState.update { it.copy(showDialog = true) }
    fun hideDialog() = localUiState.update { it.copy(showDialog = false) }
}
