package cn.super12138.todo.ui.pages.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.SettingsRepository
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.logic.model.PaletteStyle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsAppearanceViewModel(private val settingsRepository: SettingsRepository) :
    ViewModel() {
    // 把整体Ui状态流拆成3个小流以保证类型安全
    val appearanceUiState: StateFlow<SettingsAppearanceUiState> = combine(
        settingsRepository.dynamicColorFlow,
        settingsRepository.paletteStyleFlow,
        settingsRepository.darkModeFlow,
        settingsRepository.pureBlackFlow,
        settingsRepository.contrastLevelFlow,
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

    // 实验性设置单独设置流
    val previewColorSystemFlow = settingsRepository.previewColorSystemFlow

    fun setDynamicColor(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColor(value)
        }
    }

    fun setPaletteStyle(paletteStyle: PaletteStyle) {
        viewModelScope.launch {
            settingsRepository.setPaletteStyle(paletteStyle)
        }
    }

    fun setDarkMode(darkMode: DarkMode) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(darkMode)
        }
    }

    fun setPureBlackMode(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setPureBlackMode(value)
        }
    }

    fun setContrastLevel(contrastLevel: ContrastLevel) {
        viewModelScope.launch {
            settingsRepository.setContrastLevel(contrastLevel)
        }
    }

    fun setPreviewColorSystem(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setPreviewColorSystem(value)
        }
    }
}