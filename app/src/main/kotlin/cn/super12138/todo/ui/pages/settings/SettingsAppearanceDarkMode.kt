package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SwitchSettingsItem
import cn.super12138.todo.ui.pages.settings.components.appearance.darkmode.DarkModePicker
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsAppearanceDarkMode(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsAppearanceViewModel = koinViewModel()
) {
    val uiState by viewModel.appearanceUiState.collectAsStateWithLifecycle()

    TopAppBarScaffold(
        title = stringResource(R.string.pref_dark_mode),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            item(key = 1) {
                DarkModePicker(
                    currentDarkMode = uiState.darkMode,
                    onDarkModeChange = { viewModel.setDarkMode(it) }
                )
            }

            item(key = 2) {
                SwitchSettingsItem(
                    checked = uiState.pureBlackMode,
                    leadingIconRes = R.drawable.ic_wb_twilight,
                    title = stringResource(R.string.pref_pure_black_mode),
                    description = stringResource(R.string.pref_pure_black_mode_desc),
                    onCheckedChange = { viewModel.setPureBlackMode(it) }
                )
            }
        }
    }
}