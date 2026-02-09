package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.SortingMethod
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsCategory
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.pages.settings.components.SettingsPlainBox
import cn.super12138.todo.ui.pages.settings.components.SettingsRadioDialog
import cn.super12138.todo.ui.pages.settings.components.SettingsRadioOptions
import cn.super12138.todo.ui.pages.settings.components.SwitchSettingsItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsInterface(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    // val showCompleted by DataStoreManager.showCompletedFlow.collectAsState(initial = Constants.PREF_SHOW_COMPLETED_DEFAULT)
    val secureMode by DataStoreManager.secureModeFlow.collectAsState(initial = Constants.PREF_SECURE_MODE_DEFAULT)
    val sortingMethod by DataStoreManager.sortingMethodFlow.collectAsState(initial = Constants.PREF_SORTING_METHOD_DEFAULT)
    val hapticFeedback by DataStoreManager.hapticFeedbackFlow.collectAsState(initial = Constants.PREF_HAPTIC_FEEDBACK_DEFAULT)

    val scope = rememberCoroutineScope()
    var showSortingMethodDialog by rememberSaveable { mutableStateOf(false) }
    TopAppBarScaffold(
        title = stringResource(R.string.pref_interface_interaction),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            item {
                SettingsCategory(
                    title = stringResource(R.string.pref_category_todo_list),
                    first = true
                )
                /*SwitchSettingsItem(
                    leadingIconRes = R.drawable.ic_checklist,
                    title = stringResource(R.string.pref_show_completed),
                    description = stringResource(R.string.pref_show_completed_desc),
                    checked = showCompleted,
                    onCheckedChange = { scope.launch { DataStoreManager.setShowCompleted(it) } }
                )*/
                SettingsItem(
                    leadingIconRes = R.drawable.ic_sort,
                    title = stringResource(R.string.pref_sorting_method),
                    description = stringResource(SortingMethod.fromId(sortingMethod).nameRes),
                    onClick = { showSortingMethodDialog = true }
                )
            }

            item {
                SettingsCategory(stringResource(R.string.pref_category_global))
                SwitchSettingsItem(
                    checked = secureMode,
                    leadingIconRes = R.drawable.ic_shield,
                    title = stringResource(R.string.pref_secure_mode),
                    description = stringResource(R.string.pref_secure_mode_desc),
                    onCheckedChange = { scope.launch { DataStoreManager.setSecureMode(it) } }
                )
            }

            item {
                SwitchSettingsItem(
                    checked = hapticFeedback,
                    leadingIconRes = R.drawable.ic_touch_long,
                    title = stringResource(R.string.pref_haptic_feedback),
                    description = stringResource(R.string.pref_haptic_feedback_desc),
                    onCheckedChange = { scope.launch { DataStoreManager.setHapticFeedback(it) } }
                )
                SettingsPlainBox(stringResource(R.string.pref_haptic_feedback_more_info))
            }
        }

        val sortingList = SortingMethod.entries.map {
            SettingsRadioOptions(
                id = it.id,
                text = stringResource(it.nameRes)
            )
        }
        SettingsRadioDialog(
            visible = showSortingMethodDialog,
            title = stringResource(R.string.pref_sorting_method),
            currentOptions = SettingsRadioOptions(
                id = sortingMethod,
                text = stringResource(SortingMethod.fromId(sortingMethod).nameRes)
            ),
            options = sortingList,
            onSelect = { scope.launch { DataStoreManager.setSortingMethod(it) } },
            onDismiss = { showSortingMethodDialog = false }
        )
    }
}