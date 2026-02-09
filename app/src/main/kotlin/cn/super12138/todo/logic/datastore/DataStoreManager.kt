package cn.super12138.todo.logic.datastore

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cn.super12138.todo.VerveDoApp
import cn.super12138.todo.constants.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

object DataStoreManager {
    private val Context.dataStore by preferencesDataStore(
        name = Constants.SP_NAME,
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(
                    context = context,
                    sharedPreferencesName = Constants.SP_NAME,
                )
            )
        }
    )

    val dataStore = VerveDoApp.context.dataStore

    // Keys
    // 外观与个性化
    private val DYNAMIC_COLOR = booleanPreferencesKey(Constants.PREF_DYNAMIC_COLOR)
    private val PALETTE_STYLE = intPreferencesKey(Constants.PREF_PALETTE_STYLE)
    private val DARK_MODE = intPreferencesKey(Constants.PREF_DARK_MODE)
    private val CONTRAST_LEVEL = floatPreferencesKey(Constants.PREF_CONTRAST_LEVEL)

    // 界面与交互
    // private val SHOW_COMPLETED = booleanPreferencesKey(Constants.PREF_SHOW_COMPLETED)
    private val SORTING_METHOD = intPreferencesKey(Constants.PREF_SORTING_METHOD)
    private val SECURE_MODE = booleanPreferencesKey(Constants.PREF_SECURE_MODE)
    private val HAPTIC_FEEDBACK = booleanPreferencesKey(Constants.PREF_HAPTIC_FEEDBACK)

    // 数据
    private val CATEGORIES = stringPreferencesKey(Constants.PREF_CATEGORIES)

    // Getters
    val dynamicColorFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR] ?: Constants.PREF_DYNAMIC_COLOR_DEFAULT
    }

    val paletteStyleFlow = dataStore.data.map { preferences ->
        preferences[PALETTE_STYLE] ?: Constants.PREF_PALETTE_STYLE_DEFAULT
    }

    val darkModeFlow = dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: Constants.PREF_DARK_MODE_DEFAULT
    }

    val contrastLevelFlow = dataStore.data.map { preferences ->
        preferences[CONTRAST_LEVEL] ?: Constants.PREF_CONTRAST_LEVEL_DEFAULT
    }

    /*val showCompletedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_COMPLETED] ?: Constants.PREF_SHOW_COMPLETED_DEFAULT
    }*/

    val sortingMethodFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[SORTING_METHOD] ?: Constants.PREF_SORTING_METHOD_DEFAULT
    }

    val secureModeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SECURE_MODE] ?: Constants.PREF_SECURE_MODE_DEFAULT
    }

    val hapticFeedbackFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAPTIC_FEEDBACK] ?: Constants.PREF_HAPTIC_FEEDBACK_DEFAULT
    }

    val categoriesFlow: Flow<List<String>> = dataStore.data.map { preferences ->
        Json.decodeFromString(preferences[CATEGORIES] ?: Constants.PREF_CATEGORIES_DEFAULT)
    }

    // Setters
    suspend fun setDynamicColor(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR] = value
        }
    }

    suspend fun setPaletteStyle(value: Int) {
        dataStore.edit { preferences ->
            preferences[PALETTE_STYLE] = value
        }
    }

    suspend fun setDarkMode(value: Int) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = value
        }
    }

    suspend fun setContrastLevel(value: Float) {
        dataStore.edit { preferences ->
            preferences[CONTRAST_LEVEL] = value
        }
    }

    /*suspend fun setShowCompleted(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_COMPLETED] = value
        }
    }*/

    suspend fun setSortingMethod(value: Int) {
        dataStore.edit { preferences ->
            preferences[SORTING_METHOD] = value
        }
    }

    suspend fun setSecureMode(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SECURE_MODE] = value
        }
    }

    suspend fun setHapticFeedback(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAPTIC_FEEDBACK] = value
        }
    }

    suspend fun setCategories(value: List<String>) {
        dataStore.edit { preferences ->
            preferences[CATEGORIES] = Json.encodeToString(value)
        }
    }
}