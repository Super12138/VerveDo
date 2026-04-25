package cn.super12138.todo.ui.pages.settings

import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.logic.model.SortingMethod


data class SettingsAppearanceUiState(
    val dynamicColor: Boolean = false,
    val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    val darkMode: DarkMode = DarkMode.FollowSystem,
    val pureBlackMode: Boolean = false,
    val contrastLevel: ContrastLevel = ContrastLevel.Default
)

data class SettingsInterfaceUiState(
    val sortingMethod: SortingMethod = SortingMethod.Sequential,
    val textFieldAutoFocus: Boolean = false,
    val secureMode: Boolean = false,
    val hapticFeedback: Boolean = false
)

data class SettingsDataUiState(
    val categories: List<String> = emptyList()
)