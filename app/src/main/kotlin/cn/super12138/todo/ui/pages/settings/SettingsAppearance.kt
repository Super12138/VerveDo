package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.pages.settings.components.SwitchSettingsItem
import cn.super12138.todo.ui.pages.settings.components.appearance.contrast.ContrastPicker
import cn.super12138.todo.ui.pages.settings.components.appearance.palette.PalettePicker
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearance(
    modifier: Modifier = Modifier,
    toDarkModePage: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.appearanceUiState.collectAsStateWithLifecycle(SettingsAppearanceUiState())

    TopAppBarScaffold(
        title = stringResource(R.string.pref_appearance),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            item(key = 1) {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_dark_mode,
                    title = stringResource(R.string.pref_dark_mode),
                    description = stringResource(R.string.pref_dark_mode_desc),
                    onClick = toDarkModePage
                )

            }

            item(key = 2) {
                SwitchSettingsItem(
                    checked = uiState.dynamicColor,
                    leadingIconRes = R.drawable.ic_wand_stars,
                    title = stringResource(R.string.pref_appearance_dynamic_color),
                    description = stringResource(R.string.pref_appearance_dynamic_color_desc),
                    onCheckedChange = { viewModel.setDynamicColor(it) }
                )
            }

            item(key = 3) {
                PalettePicker(
                    currentPalette = { uiState.paletteStyle },
                    onPaletteChange = { viewModel.setPaletteStyle(it.id) },
                    isDynamicColor = uiState.dynamicColor,
                    darkMode = uiState.darkMode,
                    pureBlackMode = uiState.pureBlackMode,
                    contrastLevel = uiState.contrastLevel
                )
            }

            item(key = 4) {
                ContrastPicker(
                    currentContrast = uiState.contrastLevel,
                    onContrastChange = { viewModel.setContrastLevel(it.value) }
                )
            }
        }
    }
}