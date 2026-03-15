package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.pages.settings.components.SwitchSettingsItem
import cn.super12138.todo.ui.pages.settings.components.appearance.contrast.ContrastPicker
import cn.super12138.todo.ui.pages.settings.components.appearance.palette.PalettePicker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearance(
    toDarkModePage: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColor by DataStoreManager.dynamicColorFlow.collectAsState(initial = Constants.PREF_DYNAMIC_COLOR_DEFAULT)
    val darkMode by DataStoreManager.darkModeFlow.collectAsState(initial = Constants.PREF_DARK_MODE_DEFAULT)
    val pureBlackMode by DataStoreManager.pureBlackFlow.collectAsState(initial = Constants.PREF_PURE_BLACK_MODE_DEFAULT)
    val paletteStyle by DataStoreManager.paletteStyleFlow.collectAsState(initial = Constants.PREF_PALETTE_STYLE_DEFAULT)
    val contrastLevel by DataStoreManager.contrastLevelFlow.collectAsState(initial = Constants.PREF_CONTRAST_LEVEL_DEFAULT)

    val scope = rememberCoroutineScope()
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
                    checked = dynamicColor,
                    leadingIconRes = R.drawable.ic_wand_stars,
                    title = stringResource(R.string.pref_appearance_dynamic_color),
                    description = stringResource(R.string.pref_appearance_dynamic_color_desc),
                    onCheckedChange = { scope.launch { DataStoreManager.setDynamicColor(it) } }
                )
            }

            item(key = 3) {
                PalettePicker(
                    currentPalette = { PaletteStyle.fromId(paletteStyle) },
                    onPaletteChange = { scope.launch { DataStoreManager.setPaletteStyle(it.id) } },
                    isDynamicColor = dynamicColor,
                    isDarkMode = DarkMode.fromId(darkMode),
                    pureBlackMode = pureBlackMode,
                    contrastLevel = ContrastLevel.fromFloat(contrastLevel)
                )
            }

            item(key = 4) {
                ContrastPicker(
                    currentContrast = ContrastLevel.fromFloat(contrastLevel),
                    onContrastChange = { scope.launch { DataStoreManager.setContrastLevel(it.value) } }
                )
            }
        }
    }
}