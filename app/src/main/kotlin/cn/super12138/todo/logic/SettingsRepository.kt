package cn.super12138.todo.logic

import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.logic.model.SortingDirection
import cn.super12138.todo.logic.model.SortingOption
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class SettingsRepository(
    private val dataStoreManager: DataStoreManager,
    private val json: Json
) {
    val dynamicColorFlow = dataStoreManager.dynamicColorFlow
    val paletteStyleFlow = dataStoreManager.paletteStyleFlow.map { PaletteStyle.fromId(it) }
    val darkModeFlow = dataStoreManager.darkModeFlow.map { DarkMode.fromId(it) }
    val pureBlackFlow = dataStoreManager.pureBlackFlow
    val contrastLevelFlow = dataStoreManager.contrastLevelFlow.map { ContrastLevel.fromFloat(it) }
    val sortingOptionFlow = dataStoreManager.sortingOptionFlow.map { SortingOption.fromId(it) }
    val sortingDirectionFlow =
        dataStoreManager.sortingDirectionFlow.map { SortingDirection.fromId(it) }
    val textFieldAutoFocusFlow = dataStoreManager.textFieldAutoFocusFlow
    val secureModeFlow = dataStoreManager.secureModeFlow
    val hapticFeedbackFlow = dataStoreManager.hapticFeedbackFlow
    val categoriesFlow =
        dataStoreManager.categoriesFlow.map { json.decodeFromString<List<String>>(it) }

    suspend fun setDynamicColor(value: Boolean) = dataStoreManager.setDynamicColor(value)
    suspend fun setPaletteStyle(value: PaletteStyle) = dataStoreManager.setPaletteStyle(value.id)
    suspend fun setDarkMode(value: DarkMode) = dataStoreManager.setDarkMode(value.id)
    suspend fun setPureBlackMode(value: Boolean) = dataStoreManager.setPureBlackMode(value)
    suspend fun setContrastLevel(value: ContrastLevel) =
        dataStoreManager.setContrastLevel(value.value)

    suspend fun setSortingOption(value: SortingOption) = dataStoreManager.setSortingMethod(value.id)
    suspend fun setSortingDirection(value: SortingDirection) =
        dataStoreManager.setSortingDirection(value.id)

    suspend fun setTextFieldAutoFocus(value: Boolean) =
        dataStoreManager.setTextFieldAutoFocus(value)

    suspend fun setSecureMode(value: Boolean) = dataStoreManager.setSecureMode(value)
    suspend fun setHapticFeedback(value: Boolean) = dataStoreManager.setHapticFeedback(value)
    suspend fun setCategories(value: List<String>) =
        dataStoreManager.setCategories(json.encodeToString(value))
}
