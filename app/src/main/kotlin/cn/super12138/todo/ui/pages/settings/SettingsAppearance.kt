package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.pages.settings.components.SwitchSettingsItem
import cn.super12138.todo.ui.pages.settings.components.appearance.contrast.ContrastPicker
import cn.super12138.todo.ui.pages.settings.components.appearance.palette.PalettePicker
import com.kyant.m3color.dynamiccolor.ColorSpec
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsAppearance(
    modifier: Modifier = Modifier,
    toDarkModePage: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SettingsAppearanceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val previewColorSystem by viewModel.previewColorSystemFlow.collectAsStateWithLifecycle(
        initialValue = Constants.PREF_PREVIEW_COLOR_SYSTEM_DEFAULT
    )

    val specVersion = remember(previewColorSystem) {
        if (previewColorSystem) ColorSpec.SpecVersion.SPEC_2025 else ColorSpec.SpecVersion.SPEC_2021
    }

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
                    currentPalette = uiState.paletteStyle,
                    onPaletteChange = { viewModel.setPaletteStyle(it) },
                    isDynamicColor = uiState.dynamicColor,
                    darkMode = uiState.darkMode,
                    pureBlackMode = uiState.pureBlackMode,
                    contrastLevel = uiState.contrastLevel,
                    specVersion = specVersion
                )
            }

            item(key = 4) {
                ContrastPicker(
                    currentContrast = uiState.contrastLevel,
                    onContrastChange = { viewModel.setContrastLevel(it) }
                )
            }

            item(key = 5) {
                SwitchSettingsItem(
                    checked = previewColorSystem,
                    leadingIconRes = R.drawable.ic_experiment,
                    title = stringResource(R.string.pref_preview_color_system),
                    description = stringResource(R.string.pref_preview_color_system_desc),
                    onCheckedChange = { viewModel.setPreviewColorSystem(it) }
                )
            }
        }
    }
}