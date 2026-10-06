package cn.super12138.todo.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.SettingsRepository
import cn.super12138.todo.ui.pages.settings.SettingsAppearanceUiState
import cn.super12138.todo.utils.ConfettiController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val confettiController: ConfettiController
) : ViewModel() {
    val isConfettiVisible = confettiController.visible
    val secureModeFlow = settingsRepository.secureModeFlow
    val hapticFeedbackFlow = settingsRepository.hapticFeedbackFlow

    val appearanceUiState: StateFlow<SettingsAppearanceUiState> = combine(
        settingsRepository.dynamicColorFlow,
        settingsRepository.paletteStyleFlow,
        settingsRepository.darkModeFlow,
        settingsRepository.pureBlackFlow,
        settingsRepository.contrastLevelFlow
    ) { dynamicColor, paletteStyle, darkMode, pureBlackMode, contrastLevel ->
        SettingsAppearanceUiState(
            dynamicColor = dynamicColor,
            paletteStyle = paletteStyle,
            darkMode = darkMode,
            pureBlackMode = pureBlackMode,
            contrastLevel = contrastLevel
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsAppearanceUiState()
    )

    fun setConfettiVisibility(visible: Boolean) = confettiController.setVisibility(visible)
}