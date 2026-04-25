package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.SortingMethod
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsCategory
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.pages.settings.components.SettingsPlainBox
import cn.super12138.todo.ui.pages.settings.components.SettingsRadioDialog
import cn.super12138.todo.ui.pages.settings.components.SettingsRadioOptions
import cn.super12138.todo.ui.pages.settings.components.SwitchSettingsItem
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsInterface(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.interfaceUiState.collectAsStateWithLifecycle(
        initialValue = SettingsInterfaceUiState(),
        lifecycle = lifecycleOwner.lifecycle
    )

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
                    description = stringResource(uiState.sortingMethod.nameRes),
                    onClick = { showSortingMethodDialog = true }
                )
            }

            item {
                SwitchSettingsItem(
                    leadingIconRes = R.drawable.ic_eye_tracking,
                    title = stringResource(R.string.pref_text_field_auto_focus),
                    description = stringResource(R.string.pref_text_field_auto_focus_desc),
                    checked = uiState.textFieldAutoFocus,
                    onCheckedChange = { viewModel.setTextFieldAutoFocus(it) }
                )
            }

            item {
                SettingsCategory(stringResource(R.string.pref_category_global))
                SwitchSettingsItem(
                    checked = uiState.secureMode,
                    leadingIconRes = R.drawable.ic_shield,
                    title = stringResource(R.string.pref_secure_mode),
                    description = stringResource(R.string.pref_secure_mode_desc),
                    onCheckedChange = { viewModel.setSecureMode(it) }
                )
            }

            item {
                SwitchSettingsItem(
                    checked = uiState.hapticFeedback,
                    leadingIconRes = R.drawable.ic_touch_long,
                    title = stringResource(R.string.pref_haptic_feedback),
                    description = stringResource(R.string.pref_haptic_feedback_desc),
                    onCheckedChange = { viewModel.setHapticFeedback(it) }
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
                id = uiState.sortingMethod.id,
                text = stringResource(uiState.sortingMethod.nameRes)
            ),
            options = sortingList,
            onSelect = { viewModel.setSortingMethod(it) },
            onDismiss = { showSortingMethodDialog = false }
        )
    }
}