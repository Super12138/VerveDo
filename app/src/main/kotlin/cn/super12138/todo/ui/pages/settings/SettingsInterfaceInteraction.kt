package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.navigation.VerveDoDestinations
import cn.super12138.todo.ui.pages.settings.components.RadioOption
import cn.super12138.todo.ui.pages.settings.components.SettingsCategory
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.pages.settings.components.SettingsPlainBox
import cn.super12138.todo.ui.pages.settings.components.SettingsRadioDialog
import cn.super12138.todo.ui.pages.settings.components.SwitchSettingsItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsInterface(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsInterfaceInteractionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TopAppBarScaffold(
        title = stringResource(R.string.pref_interface_interaction),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            item {
                SettingsCategory(
                    title = stringResource(R.string.pref_category_editor),
                    first = true
                )
                /*SwitchSettingsItem(
                    leadingIconRes = R.drawable.ic_checklist,
                    title = stringResource(R.string.pref_show_completed),
                    description = stringResource(R.string.pref_show_completed_desc),
                    checked = showCompleted,
                    onCheckedChange = { scope.launch { DataStoreManager.setShowCompleted(it) } }
                )*/
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
                SettingsItem(
                    leadingIconRes = R.drawable.ic_tab,
                    title = stringResource(R.string.pref_startup_page),
                    description = stringResource(R.string.pref_startup_page_desc),
                    onClick = { viewModel.showDialog() }
                )
            }

            item {
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

        SettingsRadioDialog(
            visible = uiState.showDialog,
            title = stringResource(R.string.pref_startup_page),
            options = VerveDoDestinations.entries.map {
                RadioOption(
                    text = stringResource(it.label),
                    selected = it == uiState.startupPage,
                    onClick = { viewModel.setStartupPage(it) }
                )
            },
            onDismiss = { viewModel.hideDialog() },
        )
    }
}